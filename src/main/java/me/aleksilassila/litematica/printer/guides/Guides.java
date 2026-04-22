package me.aleksilassila.litematica.printer.guides;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import me.aleksilassila.litematica.printer.Printer;
import me.aleksilassila.litematica.printer.SchematicBlockState;
import me.aleksilassila.litematica.printer.guides.interaction.*;
import me.aleksilassila.litematica.printer.guides.placement.*;
import net.minecraft.block.*;


public class Guides {
  protected static final List<GuideEntry> guides = new ArrayList<>();

  private record GuideEntry(Constructor<? extends Guide> constructor,
                            Class<? extends Block>[] blocks) {
    boolean appliesTo(SchematicBlockState state) {
      if (blocks.length == 0) {
        return true;
      }

      Block targetBlock = state.targetState.getBlock();
      for (Class<? extends Block> clazz : blocks) {
        if (clazz.isInstance(targetBlock)) {
          return true;
        }
      }

      return false;
    }

    Guide create(SchematicBlockState state)
        throws ReflectiveOperationException {
      return constructor.newInstance(state);
    }
  }

  @SafeVarargs
  protected static void registerGuide(Class<? extends Guide> guideClass,
                                      Class<? extends Block>... blocks) {
    try {
      Constructor<? extends Guide> constructor =
          guideClass.getConstructor(SchematicBlockState.class);
      guides.add(new GuideEntry(constructor, blocks));
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(
          "Missing SchematicBlockState constructor for " + guideClass.getName(),
          e);
    }
  }

  static {
    // registerGuide(SkipGuide.class, AbstractSignBlock.class, SkullBlock.class,
    // BannerBlock.class);

    registerGuide(RotatingBlockGuide.class, AbstractSkullBlock.class,
                  AbstractSignBlock.class, AbstractBannerBlock.class);
    registerGuide(SlabGuide.class, SlabBlock.class);
    registerGuide(TorchGuide.class, TorchBlock.class);
    registerGuide(FarmlandGuide.class, FarmlandBlock.class);
    registerGuide(TillingGuide.class, FarmlandBlock.class);
    registerGuide(RailGuesserGuide.class, AbstractRailBlock.class);
    registerGuide(ChestGuide.class, ChestBlock.class);
    registerGuide(ContainerPlacementGuide.class, BarrelBlock.class,
                  ShulkerBoxBlock.class, AbstractFurnaceBlock.class,
                  HopperBlock.class, DropperBlock.class, DispenserBlock.class);
    registerGuide(FlowerPotGuide.class, FlowerPotBlock.class);
    registerGuide(FlowerPotFillGuide.class, FlowerPotBlock.class);
    registerGuide(TrapdoorGuide.class, TrapdoorBlock.class);

    registerGuide(PropertySpecificGuesserGuide.class, RepeaterBlock.class,
                  ComparatorBlock.class, RedstoneWireBlock.class,
                  RedstoneTorchBlock.class, BambooBlock.class,
                  CactusBlock.class, SaplingBlock.class, ScaffoldingBlock.class,
                  PointedDripstoneBlock.class, HorizontalConnectingBlock.class,
                  FenceGateBlock.class, ChestBlock.class, SnowBlock.class,
                  SeaPickleBlock.class, CandleBlock.class, LeverBlock.class,
                  EndPortalFrameBlock.class, NoteBlock.class,
                  CampfireBlock.class, PoweredRailBlock.class,
                  LeavesBlock.class, TripwireHookBlock.class);
    registerGuide(DoorPlacementGuide.class, DoorBlock.class);
    registerGuide(FallingBlockGuide.class, FallingBlock.class);
    registerGuide(BlockIndifferentGuesserGuide.class, BambooBlock.class,
                  BigDripleafStemBlock.class, BigDripleafBlock.class,
                  TwistingVinesPlantBlock.class, TripwireBlock.class);

    registerGuide(CampfireExtinguishGuide.class, CampfireBlock.class);
    registerGuide(LightCandleGuide.class, AbstractCandleBlock.class);
    registerGuide(EnderEyeGuide.class, EndPortalFrameBlock.class);
    registerGuide(CycleStateGuide.class, DoorBlock.class, FenceGateBlock.class,
                  TrapdoorBlock.class, LeverBlock.class, RepeaterBlock.class,
                  ComparatorBlock.class, NoteBlock.class);
    registerGuide(BlockReplacementGuide.class, SnowBlock.class,
                  SeaPickleBlock.class, CandleBlock.class, SlabBlock.class);
    registerGuide(LogGuide.class);
    registerGuide(LogStrippingGuide.class);
    registerGuide(GuesserGuide.class);
  }

  private List<GuideEntry> getGuides() { return guides; }

  public Guide[] getInteractionGuides(SchematicBlockState state) {
    List<GuideEntry> guides = getGuides();

    ArrayList<Guide> applicableGuides = new ArrayList<>();
    for (GuideEntry guideEntry : guides) {
      try {
        if (guideEntry.appliesTo(state)) {
          applicableGuides.add(guideEntry.create(state));
        }
      } catch (ReflectiveOperationException e) {
        Printer.printDebug("Failed to build guide for {}: {}", state,
                           e.getMessage());
      }
    }

    return applicableGuides.toArray(Guide[] ::new);
  }
}
