package me.aleksilassila.litematica.printer.guides.placement;

import javax.annotation.Nullable;
import me.aleksilassila.litematica.printer.SchematicBlockState;
import me.aleksilassila.litematica.printer.config.Configs;
import me.aleksilassila.litematica.printer.implementation.PrinterPlacementContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * This is the placement guide that most blocks will use.
 * It will try to predict the correct player state for producing the right
 * blockState
 * by brute forcing the correct hit vector and look a direction.
 */
public class GuesserGuide extends GeneralPlacementGuide {
  private PrinterPlacementContext contextCache = null;

  protected static Direction[] directionsToTry =
      new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST,
                       Direction.WEST,  Direction.UP,    Direction.DOWN};
  protected static Vec3d[] hitVecsToTry = new Vec3d[] {
      new Vec3d(-0.25, -0.25, -0.25), new Vec3d(+0.25, -0.25, -0.25),
      new Vec3d(-0.25, +0.25, -0.25), new Vec3d(-0.25, -0.25, +0.25),
      new Vec3d(+0.25, +0.25, -0.25), new Vec3d(-0.25, +0.25, +0.25),
      new Vec3d(+0.25, -0.25, +0.25), new Vec3d(+0.25, +0.25, +0.25),
  };

  public GuesserGuide(SchematicBlockState state) { super(state); }

  @Nullable
  @Override
  public PrinterPlacementContext
  getPlacementContext(ClientPlayerEntity player) {
    if (contextCache != null && !Configs.PRINT_DEBUG.getBooleanValue())
      return contextCache;

    ItemStack requiredItem = getRequiredItem(player).orElse(ItemStack.EMPTY);
    int slot = getRequiredItemStackSlot(player);

    if (slot == -1)
      return null;

    for (Direction lookDirection : directionsToTry) {
      for (Direction side : directionsToTry) {
        BlockPos neighborPos = state.blockPos.offset(side);
        BlockState neighborState = state.world.getBlockState(neighborPos);
        boolean requiresShift = getRequiresExplicitShift() ||
                                isInteractive(neighborState.getBlock());

        if (!canBeClicked(state.world, neighborPos) ||
            neighborState.isReplaceable())
          continue;

        Vec3d hitVec = Vec3d.ofCenter(state.blockPos)
                           .add(Vec3d.of(side.getVector()).multiply(0.5));

        for (Vec3d hitVecToTry : hitVecsToTry) {
          Vec3d multiplier = Vec3d.of(side.getVector());
          multiplier =
              new Vec3d(multiplier.x == 0 ? 1 : 0, multiplier.y == 0 ? 1 : 0,
                        multiplier.z == 0 ? 1 : 0);

          BlockHitResult hitResult =
              new BlockHitResult(hitVec.add(hitVecToTry.multiply(multiplier)),
                                 side.getOpposite(), neighborPos, false);
          PrinterPlacementContext context =
              new PrinterPlacementContext(player, hitResult, requiredItem, slot,
                                          lookDirection, requiresShift);
          BlockState result = getRequiredItemAsBlock(player)
                                  .orElse(targetState.getBlock())
                                  .getPlacementState(context);

          if (Configs.GUESSER_TORCH_FIX_EXPERIMENTAL.getBooleanValue() &&
              !isValidExperimentalTorchGuess(result, neighborState)) {
            continue;
          }

          if (result != null && (statesEqual(result, targetState) ||
                                 correctChestPlacement(targetState, result))) {
            contextCache = context;
            return context;
          }
        }
      }
    }

    if (allowAirPlace()) {
      for (Direction lookDirection : directionsToTry) {
        for (Direction side : directionsToTry) {
          Vec3d hitVec = Vec3d.ofCenter(state.blockPos)
                             .add(Vec3d.of(side.getVector()).multiply(0.5));

          for (Vec3d hitVecToTry : hitVecsToTry) {
            Vec3d multiplier = Vec3d.of(side.getVector());
            multiplier =
                new Vec3d(multiplier.x == 0 ? 1 : 0, multiplier.y == 0 ? 1 : 0,
                          multiplier.z == 0 ? 1 : 0);

            BlockHitResult hitResult =
                new BlockHitResult(hitVec.add(hitVecToTry.multiply(multiplier)),
                                   side.getOpposite(), state.blockPos, false);
            PrinterPlacementContext context = new PrinterPlacementContext(
                player, hitResult, requiredItem, slot, lookDirection,
                getRequiresExplicitShift());
            BlockState result = getRequiredItemAsBlock(player)
                                    .orElse(targetState.getBlock())
                                    .getPlacementState(context);

            if (result != null && statesEqual(result, targetState)) {
              contextCache = context;
              return context;
            }
          }
        }
      }
    }

    return null;
  }

  @Override
  public boolean canExecute(ClientPlayerEntity player) {
    if (targetState.getBlock() instanceof SlabBlock)
      return false;

    return super.canExecute(player);
  }

  private boolean correctChestPlacement(BlockState targetState,
                                        BlockState result) {
    if (targetState.contains(ChestBlock.CHEST_TYPE) &&
        result.contains(ChestBlock.CHEST_TYPE) &&
        result.get(ChestBlock.FACING) == targetState.get(ChestBlock.FACING)) {
      ChestType targetChestType = targetState.get(ChestBlock.CHEST_TYPE);
      ChestType resultChestType = result.get(ChestBlock.CHEST_TYPE);

      return targetChestType != ChestType.SINGLE &&
          resultChestType == ChestType.SINGLE;
    }

    return false;
  }

  private boolean isValidExperimentalTorchGuess(@Nullable BlockState result,
                                                BlockState neighborState) {
    if (result == null) {
      return false;
    }

    if (!result.canPlaceAt(state.world, state.blockPos)) {
      return false;
    }

    boolean torchLikeTarget = targetState.getBlock() instanceof TorchBlock ||
                              targetState.getBlock() instanceof WallTorchBlock;
    boolean clickedTorchLike =
        neighborState.getBlock() instanceof TorchBlock ||
        neighborState.getBlock() instanceof WallTorchBlock;

    return !(torchLikeTarget && clickedTorchLike);
  }
}
