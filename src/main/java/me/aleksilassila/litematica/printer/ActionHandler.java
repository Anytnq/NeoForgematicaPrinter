package me.aleksilassila.litematica.printer;

import me.aleksilassila.litematica.printer.actions.Action;
import me.aleksilassila.litematica.printer.actions.PrepareAction;
import me.aleksilassila.litematica.printer.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.ArrayDeque;
import java.util.Queue;

public class ActionHandler {
    private final MinecraftClient client;
    private final ClientPlayerEntity player;
    private final Queue<Action> actionQueue = new ArrayDeque<>();
    public PrepareAction lookAction = null;
    private int tick = 0;

    public ActionHandler(MinecraftClient client, ClientPlayerEntity player) {
        this.client = client;
        this.player = player;
    }

    public void onGameTick() {
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

    public boolean acceptsActions() {
        return actionQueue.isEmpty();
    }

    public void addActions(Action... actions) {
        if (actions == null || actions.length == 0 || !acceptsActions()) {
            return;
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
