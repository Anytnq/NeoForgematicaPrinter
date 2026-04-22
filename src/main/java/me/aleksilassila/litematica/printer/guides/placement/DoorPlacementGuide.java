package me.aleksilassila.litematica.printer.guides.placement;

import java.util.List;
import me.aleksilassila.litematica.printer.SchematicBlockState;
import me.aleksilassila.litematica.printer.actions.Action;
import me.aleksilassila.litematica.printer.actions.DelayAction;
import net.minecraft.client.network.ClientPlayerEntity;


/**
 * Placement guide for doors – identical to PropertySpecificGuesserGuide but
 * appends two delay slots after placement so that CycleStateGuide does not
 * toggle the door state again immediately after it was placed.
 */
public class DoorPlacementGuide extends PropertySpecificGuesserGuide {
  public DoorPlacementGuide(SchematicBlockState state) { super(state); }

  @Override
  public List<Action> execute(ClientPlayerEntity player) {
    List<Action> actions = super.execute(player);
    actions.add(new DelayAction());
    actions.add(new DelayAction());
    return actions;
  }
}
