package me.aleksilassila.litematica.printer.guides.placement;

import java.util.List;
import me.aleksilassila.litematica.printer.SchematicBlockState;
import me.aleksilassila.litematica.printer.actions.Action;
import me.aleksilassila.litematica.printer.actions.DelayAction;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.state.property.Properties;


public class TrapdoorGuide extends GuesserGuide {
  public TrapdoorGuide(SchematicBlockState state) { super(state); }

  @Override
  public List<Action> execute(ClientPlayerEntity player) {
    List<Action> actions = super.execute(player);
    actions.add(new DelayAction());
    actions.add(new DelayAction());
    return actions;
  }

  @Override
  protected boolean statesEqual(BlockState resultState,
                                BlockState targetState) {
    if (!(targetState.getBlock() instanceof TrapdoorBlock)) {
      return super.statesEqual(resultState, targetState);
    }

    return statesEqualIgnoreProperties(resultState, targetState,
                                       Properties.OPEN, Properties.POWERED);
  }
}
