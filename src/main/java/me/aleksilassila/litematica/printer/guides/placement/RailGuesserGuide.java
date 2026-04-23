package me.aleksilassila.litematica.printer.guides.placement;

import java.util.*;
import me.aleksilassila.litematica.printer.SchematicBlockState;
import me.aleksilassila.litematica.printer.config.Configs;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class RailGuesserGuide extends GuesserGuide {
  static final RailShape[] STRAIGHT_RAIL_SHAPES =
      new RailShape[] {RailShape.NORTH_SOUTH, RailShape.EAST_WEST};

  public RailGuesserGuide(SchematicBlockState state) { super(state); }

  @Override
  public boolean skipOtherGuides() {
    return true;
  }

  @Override
  protected boolean statesEqual(BlockState resultState,
                                BlockState targetState) {
    if (!Configs.RAIL_GUESSER_FIX_EXPERIMENTAL.getBooleanValue()) {
      return statesEqualLegacy(resultState, targetState);
    }

    return statesEqualExperimental(resultState, targetState);
  }

  private boolean statesEqualLegacy(BlockState resultState,
                                    BlockState targetState) {
    if (!wouldConnectCorrectlyLegacy())
      return false;

    if (getRailShape(resultState).isPresent()) {
      if (Arrays.stream(STRAIGHT_RAIL_SHAPES)
              .anyMatch(
                  shape -> shape == getRailShape(resultState).orElse(null))) {
        return super.statesEqualIgnoreProperties(
            resultState, targetState, Properties.RAIL_SHAPE,
            Properties.STRAIGHT_RAIL_SHAPE, Properties.POWERED);
      }
    }

    return super.statesEqual(resultState, targetState);
  }

  private boolean statesEqualExperimental(BlockState resultState,
                                          BlockState targetState) {
    Optional<RailShape> targetShape = getRailShape(targetState);
    Optional<RailShape> resultShape = getRailShape(resultState);

    if (targetShape.isEmpty() || resultShape.isEmpty()) {
      return false;
    }

    if (!wouldConnectCorrectlyExperimental(targetShape.get())) {
      return false;
    }

    if (wouldBlockAnotherConnection(targetShape.get(), resultShape.get())) {
      return false;
    }

    if (isStraightShape(resultShape.get())) {
      return super.statesEqualIgnoreProperties(
          resultState, targetState, Properties.RAIL_SHAPE,
          Properties.STRAIGHT_RAIL_SHAPE, Properties.POWERED);
    }

    return super.statesEqual(resultState, targetState);
  }

  private boolean wouldConnectCorrectlyLegacy() {
    RailShape targetShape = getRailShape(state.targetState).orElse(null);
    if (targetShape == null)
      return false;

    List<Direction> allowedConnections = getRailDirections(targetShape);

    List<Direction> possibleConnections = new ArrayList<>();
    for (Direction d : Direction.values()) {
      if (d.getAxis().isVertical())
        continue;
      SchematicBlockState neighbor = state.offset(d);

      if (hasFreeConnections(neighbor)) {
        possibleConnections.add(d);
      }
    }

    if (possibleConnections.size() > 2)
      return false;

    return new HashSet<>(allowedConnections).containsAll(possibleConnections);
  }

  private boolean wouldConnectCorrectlyExperimental(RailShape targetShape) {
    List<Direction> allowedConnections = getRailDirections(targetShape);
    List<Direction> possibleConnections = new ArrayList<>();

    for (Direction d : Direction.values()) {
      if (d.getAxis().isVertical()) {
        continue;
      }

      SchematicBlockState neighbor = state.offset(d);
      if (hasFreeConnectionsExperimental(neighbor)) {
        possibleConnections.add(d);
      }
    }

    if (possibleConnections.size() > 2) {
      return false;
    }

    return new HashSet<>(allowedConnections).containsAll(possibleConnections);
  }

  private boolean hasFreeConnections(SchematicBlockState state) {
    List<Direction> possibleConnections = getRailDirections(state);
    if (possibleConnections.isEmpty())
      return false;

    for (Direction d : possibleConnections) {
      SchematicBlockState neighbor = state.offset(d);
      if (neighbor.currentState.getBlock() != neighbor.targetState.getBlock()) {
        return false;
      }
    }

    return possibleConnections.stream().anyMatch(possibleDirection -> {
      SchematicBlockState neighbor = state.offset(possibleDirection);
      return !getRailDirections(neighbor).contains(
          possibleDirection.getOpposite());
    });
  }

  private boolean hasFreeConnectionsExperimental(SchematicBlockState state) {
    List<Direction> possibleConnections = getRailDirections(state);
    if (possibleConnections.isEmpty()) {
      return false;
    }

    for (Direction d : possibleConnections) {
      SchematicBlockState neighbor = state.offset(d);
      if (neighbor.currentState.getBlock() != neighbor.targetState.getBlock()) {
        return false;
      }
    }

    return possibleConnections.stream().anyMatch(possibleDirection -> {
      SchematicBlockState neighbor = state.offset(possibleDirection);
      return !getRailDirections(neighbor).contains(
          possibleDirection.getOpposite());
    });
  }

  private boolean wouldBlockAnotherConnection(RailShape targetShape,
                                              RailShape resultShape) {
    Set<Direction> targetConnections =
        new HashSet<>(getRailDirections(targetShape));
    Set<Direction> resultConnections =
        new HashSet<>(getRailDirections(resultShape));

    for (Direction d : Direction.values()) {
      if (d.getAxis().isVertical()) {
        continue;
      }

      if (!resultConnections.contains(d)) {
        continue;
      }

      if (targetConnections.contains(d)) {
        continue;
      }

      SchematicBlockState neighbor = state.offset(d);
      if (!getRailShape(neighbor.targetState).isPresent()) {
        continue;
      }

      if (isFutureRailConnectionUnstable(neighbor)) {
        return true;
      }
    }

    return false;
  }

  private boolean isFutureRailConnectionUnstable(SchematicBlockState neighbor) {
    if (!super.statesEqual(neighbor.currentState, neighbor.targetState)) {
      return true;
    }

    RailShape shape = getRailShape(neighbor.targetState).orElse(null);
    return shape != null && !shape.isAscending();
  }

  private boolean isStraightShape(RailShape shape) {
    return Arrays.stream(STRAIGHT_RAIL_SHAPES)
        .anyMatch(known -> known == shape);
  }

  private List<Direction> getRailDirections(SchematicBlockState state) {
    RailShape shape = getRailShape(state.currentState).orElse(null);
    if (shape == null)
      return new ArrayList<>();

    return getRailDirections(shape);
  }

  private List<Direction> getRailDirections(RailShape railShape) {
    String name = railShape.getName();

    if (railShape.isAscending()) {
      Direction d =
          Direction.valueOf(name.replace("ascending_", "").toUpperCase());
      return Arrays.asList(d, d.getOpposite());
    } else {
      Direction d1 = Direction.valueOf(name.split("_")[0].toUpperCase());
      Direction d2 = Direction.valueOf(name.split("_")[1].toUpperCase());
      return Arrays.asList(d1, d2);
    }
  }

  Optional<RailShape> getRailShape(BlockState state) {
    Optional<RailShape> shape = getProperty(state, Properties.RAIL_SHAPE);
    if (shape.isEmpty())
      return getProperty(state, Properties.STRAIGHT_RAIL_SHAPE);
    return shape;
  }
}
