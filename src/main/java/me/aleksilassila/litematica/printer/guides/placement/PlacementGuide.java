package me.aleksilassila.litematica.printer.guides.placement;

import fi.dy.masa.litematica.util.ItemUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.aleksilassila.litematica.printer.SchematicBlockState;
import me.aleksilassila.litematica.printer.actions.Action;
import me.aleksilassila.litematica.printer.actions.PrepareAction;
import me.aleksilassila.litematica.printer.actions.ReleaseShiftAction;
import me.aleksilassila.litematica.printer.config.Configs;
import me.aleksilassila.litematica.printer.guides.Guide;
import me.aleksilassila.litematica.printer.implementation.PrinterPlacementContext;
import me.aleksilassila.litematica.printer.implementation.actions.InteractActionImpl;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

abstract public class PlacementGuide extends Guide {
  public PlacementGuide(SchematicBlockState state) { super(state); }

  protected ItemStack getBlockItem(BlockState state) {
    return ItemUtils.getItemForBlock(this.state.world, this.state.blockPos,
                                     state, true);
  }

  protected Optional<Block> getRequiredItemAsBlock(ClientPlayerEntity player) {
    Optional<ItemStack> requiredItem = getRequiredItem(player);

    if (requiredItem.isEmpty()) {
      return Optional.empty();
    } else {
      ItemStack itemStack = requiredItem.get();

      if (itemStack.getItem() instanceof BlockItem)
        return Optional.of(((BlockItem)itemStack.getItem()).getBlock());
      else
        return Optional.empty();
    }
  }

  @Override
  protected @Nonnull List<ItemStack> getRequiredItems() {
    return Collections.singletonList(getBlockItem(state.targetState));
  }

  abstract protected boolean getUseShift(SchematicBlockState state);

  @Nullable
  abstract public PrinterPlacementContext
  getPlacementContext(ClientPlayerEntity player);

  @Override
  public boolean canExecute(ClientPlayerEntity player) {
    if (!super.canExecute(player))
      return false;

    Block targetBlock = targetState.getBlock();
    if (Configs.DISABLE_PLACING_DOORS_TRAPDOORS.getBooleanValue() &&
        (targetBlock instanceof DoorBlock ||
         targetBlock instanceof TrapdoorBlock ||
         targetBlock instanceof FenceGateBlock))
      return false;

    if (Configs.DISABLE_PLACING_CHESTS_CONTAINERS.getBooleanValue() &&
        (targetBlock instanceof AbstractChestBlock ||
         targetBlock instanceof BarrelBlock ||
         targetBlock instanceof ShulkerBoxBlock))
      return false;

    if (Configs.DISABLE_PLACING_STAIRS.getBooleanValue() &&
        targetBlock instanceof StairsBlock)
      return false;

    if (Configs.DISABLE_PLACING_SLABS.getBooleanValue() &&
        targetBlock instanceof SlabBlock)
      return false;

    List<ItemStack> requiredItems = getRequiredItems();
    if (requiredItems.isEmpty() ||
        requiredItems.stream().allMatch(i -> i.isOf(Items.AIR)))
      return false;

    ItemPlacementContext ctx = getPlacementContext(player);
    if (ctx == null || !ctx.canPlace())
      return false;
    if (!Configs.REPLACE_FLUIDS_SOURCE_BLOCKS.getBooleanValue() &&
        getProperty(state.currentState, FluidBlock.LEVEL).orElse(1) == 0)
      return false;

    BlockState resultState = getRequiredItemAsBlock(player)
                                 .orElse(targetState.getBlock())
                                 .getPlacementState(ctx);

    if (resultState != null) {
      if (!resultState.canPlaceAt(state.world, state.blockPos))
        return false;
      return !(currentState.getBlock() instanceof FluidBlock) ||
          canPlaceInWater(resultState);
    } else {
      return false;
    }
  }

  @Override
  public @Nonnull List<Action> execute(ClientPlayerEntity player) {
    List<Action> actions = new ArrayList<>();
    PrinterPlacementContext ctx = getPlacementContext(player);

    if (ctx == null)
      return actions;
    actions.add(new PrepareAction(ctx));
    actions.add(new InteractActionImpl(ctx));
    if (ctx.shouldSneak)
      actions.add(new ReleaseShiftAction());

    return actions;
  }

  protected static boolean canBeClicked(World world, BlockPos pos) {
    if (getOutlineShape(world, pos) == VoxelShapes.empty()) {
      return false;
    }

    Block block = world.getBlockState(pos).getBlock();

    if (block instanceof AbstractSignBlock &&
        !Configs.SIGN_CLICK_FIX_EXPERIMENTAL.getBooleanValue()) {
      return false;
    }

    return true;
  }

  private static VoxelShape getOutlineShape(World world, BlockPos pos) {
    return world.getBlockState(pos).getOutlineShape(world, pos);
  }

  public boolean isInteractive(Block block) {
    for (Class<?> clazz : interactiveBlocks) {
      if (clazz.isInstance(block)) {
        return true;
      }
    }

    return false;
  }

  @SuppressWarnings("deprecation")
  private boolean canPlaceInWater(BlockState blockState) {
    Block block = blockState.getBlock();
    if (block instanceof FluidFillable) {
      return true;
    } else if (!(block instanceof DoorBlock) &&
               !(blockState.getBlock() instanceof AbstractSignBlock) &&
               !blockState.isOf(Blocks.LADDER) &&
               !blockState.isOf(Blocks.SUGAR_CANE) &&
               !blockState.isOf(Blocks.BUBBLE_COLUMN)) {
      return blockState.blocksMovement();
    }

    return true;
  }
}
