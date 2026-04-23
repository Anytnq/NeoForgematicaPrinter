package me.aleksilassila.litematica.printer.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import java.util.List;
import me.aleksilassila.litematica.printer.PrinterReference;
import me.aleksilassila.litematica.printer.integration.overlay.OverlayPosition;

public class Configs {
  private static final String GENERIC_KEY =
      PrinterReference.MOD_KEY + ".config.generic";
  private static final String OVERLAY_KEY =
      PrinterReference.MOD_KEY + ".config.overlay";

  public static final ConfigInteger PRINTING_INTERVAL =
      new ConfigInteger("printingInterval", 12, 1, 40).apply(GENERIC_KEY);
  public static final ConfigInteger DUPLICATE_PLACE_COOLDOWN_TICKS =
      new ConfigInteger("duplicatePlaceCooldownTicks", 12, 0, 80)
          .apply(GENERIC_KEY);
  public static final ConfigDouble PRINTING_RANGE =
      new ConfigDouble("printingRange", 5, 2.5, 5).apply(GENERIC_KEY);
  public static final ConfigBoolean PRINT_MODE =
      new ConfigBoolean("printingMode", false).apply(GENERIC_KEY);
  public static final ConfigBoolean PRINT_DEBUG =
      new ConfigBoolean("printingDebug", false).apply(GENERIC_KEY);
  public static final ConfigBoolean REPLACE_FLUIDS_SOURCE_BLOCKS =
      new ConfigBoolean("replaceFluidSourceBlocks", true).apply(GENERIC_KEY);
  public static final ConfigBoolean STRIP_LOGS =
      new ConfigBoolean("stripLogs", true).apply(GENERIC_KEY);
  public static final ConfigBoolean INTERACT_BLOCKS =
      new ConfigBoolean("interactBlocks", true).apply(GENERIC_KEY);
  public static final ConfigBoolean BLOCK_ORIENTATION_FIX_EXPERIMENTAL =
      new ConfigBoolean("blockOrientationFixExperimental", false)
          .apply(GENERIC_KEY);
  public static final ConfigBoolean GUESSER_TORCH_FIX_EXPERIMENTAL =
      new ConfigBoolean("guesserTorchFixExperimental", false)
          .apply(GENERIC_KEY);
  public static final ConfigBoolean SIGN_CLICK_FIX_EXPERIMENTAL =
      new ConfigBoolean("signClickFixExperimental", false).apply(GENERIC_KEY);
  public static final ConfigBoolean RAIL_GUESSER_FIX_EXPERIMENTAL =
      new ConfigBoolean("railGuesserFixExperimental", false).apply(GENERIC_KEY);
  public static final ConfigBoolean DISABLE_PLACING_DOORS_TRAPDOORS =
      new ConfigBoolean("disablePlacingDoorsTrapdoors", false)
          .apply(GENERIC_KEY);
  public static final ConfigBoolean DISABLE_PLACING_CHESTS_CONTAINERS =
      new ConfigBoolean("disablePlacingChestsContainers", false)
          .apply(GENERIC_KEY);
  public static final ConfigBoolean DISABLE_PLACING_STAIRS =
      new ConfigBoolean("disablePlacingStairs", false).apply(GENERIC_KEY);
  public static final ConfigBoolean DISABLE_PLACING_SLABS =
      new ConfigBoolean("disablePlacingSlabs", false).apply(GENERIC_KEY);
  public static final ConfigBoolean ALLOW_AIR_PLACE =
      new ConfigBoolean("allowAirPlace", false).apply(GENERIC_KEY);

  public static final ConfigBoolean USE_MATICA_OVERLAY =
      new ConfigBoolean("useMaticaOverlay", true).apply(OVERLAY_KEY);
  public static final ConfigInteger OVERLAY_FONT_SIZE =
      new ConfigInteger("overlayFontSize", 9, 6, 24).apply(OVERLAY_KEY);
  public static final ConfigColor OVERLAY_BACKGROUND_COLOR =
      new ConfigColor("overlayBackgroundColor", "#101318").apply(OVERLAY_KEY);
  public static final ConfigColor OVERLAY_FONT_COLOR =
      new ConfigColor("overlayFontColor", "#E6ECF2").apply(OVERLAY_KEY);
  public static final ConfigOptionList OVERLAY_POSITION =
      new ConfigOptionList("overlayPosition", OverlayPosition.TOP_CENTER)
          .apply(OVERLAY_KEY);
  public static final ConfigInteger OVERLAY_TRANSPARENCY =
      new ConfigInteger("overlayTransparency", 204, 0, 255).apply(OVERLAY_KEY);

  public static ImmutableList<IConfigBase> getConfigList() {
    List<IConfigBase> list = new java.util.ArrayList<>(
        fi.dy.masa.litematica.config.Configs.Generic.OPTIONS);
    list.add(PRINT_MODE);
    list.add(PRINT_DEBUG);
    list.add(PRINTING_INTERVAL);
    list.add(DUPLICATE_PLACE_COOLDOWN_TICKS);
    list.add(PRINTING_RANGE);
    list.add(REPLACE_FLUIDS_SOURCE_BLOCKS);
    list.add(STRIP_LOGS);
    list.add(INTERACT_BLOCKS);
    list.add(BLOCK_ORIENTATION_FIX_EXPERIMENTAL);
    list.add(GUESSER_TORCH_FIX_EXPERIMENTAL);
    list.add(SIGN_CLICK_FIX_EXPERIMENTAL);
    list.add(RAIL_GUESSER_FIX_EXPERIMENTAL);
    list.add(DISABLE_PLACING_DOORS_TRAPDOORS);
    list.add(DISABLE_PLACING_CHESTS_CONTAINERS);
    list.add(DISABLE_PLACING_STAIRS);
    list.add(DISABLE_PLACING_SLABS);
    list.add(ALLOW_AIR_PLACE);

    return ImmutableList.copyOf(list);
  }

  public static ImmutableList<IConfigBase> getOverlayConfigList() {
    List<IConfigBase> list = new java.util.ArrayList<>(
        fi.dy.masa.litematica.config.Configs.InfoOverlays.OPTIONS);
    list.add(USE_MATICA_OVERLAY);
    list.add(OVERLAY_FONT_SIZE);
    list.add(OVERLAY_BACKGROUND_COLOR);
    list.add(OVERLAY_FONT_COLOR);
    list.add(OVERLAY_POSITION);
    list.add(OVERLAY_TRANSPARENCY);

    return ImmutableList.copyOf(list);
  }
}
