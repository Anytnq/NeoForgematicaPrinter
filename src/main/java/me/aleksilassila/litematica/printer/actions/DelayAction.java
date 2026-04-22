package me.aleksilassila.litematica.printer.actions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Action that does nothing – used to insert a deliberate delay
 * (one printing interval) in the action queue between placement and
 * the next interaction step.
 */
public class DelayAction extends Action {
  @Override
  public void send(MinecraftClient client, ClientPlayerEntity player) {
    // intentionally empty – just burns one printing-interval slot
  }
}
