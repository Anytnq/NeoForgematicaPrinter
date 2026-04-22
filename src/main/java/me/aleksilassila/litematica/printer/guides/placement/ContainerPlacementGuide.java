package me.aleksilassila.litematica.printer.guides.placement;

import me.aleksilassila.litematica.printer.SchematicBlockState;

/**
 * Placement guide for container blocks (barrels, shulker boxes, etc.).
 * Forces sneaking during placement so the container does not open after being
 * placed.
 */
public class ContainerPlacementGuide extends GuesserGuide {
  public ContainerPlacementGuide(SchematicBlockState state) { super(state); }

  @Override
  protected boolean getRequiresExplicitShift() {
    return true;
  }
}
