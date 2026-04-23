package me.aleksilassila.litematica.printer;

import java.util.ArrayDeque;
import java.util.Queue;
import me.aleksilassila.litematica.printer.actions.Action;
import me.aleksilassila.litematica.printer.actions.PrepareAction;
import me.aleksilassila.litematica.printer.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;


public class ActionHandler {
  private final MinecraftClient client;
  private final ClientPlayerEntity player;
  private final Queue<Action> actionQueue = new ArrayDeque<>();
  public PrepareAction lookAction = null;
  private int tick = 0;
  private long gameTickCounter = 0;
  private BlockPos lastQueuedPlacementPos = null;
  private long lastQueuedPlacementTick = Long.MIN_VALUE;

  public ActionHandler(MinecraftClient client, ClientPlayerEntity player) {
    this.client = client;
    this.player = player;
  }

  public void onGameTick() {
    gameTickCounter++;

    int tickRate = Math.max(1, Configs.PRINTING_INTERVAL.getIntegerValue());
    tick = (tick + 1) % tickRate;

    if (tick != 0) {
      return;
    }

    Action nextAction = actionQueue.poll();

    if (nextAction != null) {
      Printer.printDebug("Sending action {}", nextAction);
      nextAction.send(client, player);
    } else {
      lookAction = null;
    }
  }

  public boolean acceptsActions() { return actionQueue.isEmpty(); }

  public void addActions(Action... actions) {
    if (actions == null || actions.length == 0 || !acceptsActions()) {
      return;
    }

    PrepareAction placementPrepareAction = null;
    for (Action action : actions) {
      if (action instanceof PrepareAction prepareAction) {
        placementPrepareAction = prepareAction;
        break;
      }
    }

    if (placementPrepareAction != null) {
      BlockPos placementPos = placementPrepareAction.context.getBlockPos();
      long ticksSinceLastPlacement = gameTickCounter - lastQueuedPlacementTick;
      int duplicateCooldownTicks =
          Math.max(0, Configs.DUPLICATE_PLACE_COOLDOWN_TICKS.getIntegerValue());

      if (placementPos.equals(lastQueuedPlacementPos) &&
          ticksSinceLastPlacement <= duplicateCooldownTicks) {
        Printer.printDebug("Skipping duplicate placement for {} after {} ticks",
                           placementPos, ticksSinceLastPlacement);
        return;
      }

      lastQueuedPlacementPos = placementPos.toImmutable();
      lastQueuedPlacementTick = gameTickCounter;
    }

    for (Action action : actions) {
      if (action == null) {
        continue;
      }

      if (action instanceof PrepareAction prepareAction) {
        lookAction = prepareAction;
      }

      actionQueue.offer(action);
    }
  }
}
