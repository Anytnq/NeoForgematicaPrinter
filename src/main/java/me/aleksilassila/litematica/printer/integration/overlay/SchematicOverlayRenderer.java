package me.aleksilassila.litematica.printer.integration.overlay;

import fi.dy.masa.litematica.util.RayTraceUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.aleksilassila.litematica.printer.config.Configs;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public final class SchematicOverlayRenderer {
  private static final int BORDER_DEFAULT_COLOR = 0xFF3D7EA6;

  private SchematicOverlayRenderer() {}

  public static void render(DrawContext drawContext) {
    if (!Configs.USE_MATICA_OVERLAY.getBooleanValue()) {
      return;
    }

    MinecraftClient client = MinecraftClient.getInstance();
    ClientPlayerEntity player = client.player;
    if (player == null || client.textRenderer == null) {
      return;
    }

    WorldSchematic schematicWorld = SchematicWorldHandler.getSchematicWorld();
    if (schematicWorld == null) {
      return;
    }

    BlockHitResult schematicHit =
        RayTraceUtils.traceToSchematicWorld(player, 10, true, true);
    if (schematicHit == null) {
      return;
    }

    BlockPos targetPos = schematicHit.getBlockPos();
    BlockState targetState = schematicWorld.getBlockState(targetPos);
    if (targetState.isAir()) {
      return;
    }

    BlockState worldState = player.getWorld().getBlockState(targetPos);
    if (worldState.equals(targetState)) {
      return;
    }

    String requiredLine =
        Text.translatable("litematica-printer.overlay.requiredBlock",
                          targetState.getBlock().getName())
            .getString();
    String stateSummary = getTargetStateSummary(targetState);

    List<String> lines = new ArrayList<>();
    lines.add(requiredLine);
    if (!stateSummary.isEmpty()) {
      lines.add(Text.translatable("litematica-printer.overlay.targetState",
                                  stateSummary)
                    .getString());
    }

    float fontScale =
        Math.max(0.6f, Configs.OVERLAY_FONT_SIZE.getIntegerValue() /
                           (float)client.textRenderer.fontHeight);
    int backgroundColor =
        withAlpha(Configs.OVERLAY_BACKGROUND_COLOR.getIntegerValue(),
                  Configs.OVERLAY_TRANSPARENCY.getIntegerValue());
    int textColor = withFullAlpha(Configs.OVERLAY_FONT_COLOR.getIntegerValue());
    int borderColor =
        withAlpha(BORDER_DEFAULT_COLOR,
                  Math.max(80, Configs.OVERLAY_TRANSPARENCY.getIntegerValue()));
    ItemStack displayStack = new ItemStack(targetState.getBlock().asItem());

    int maxWidth = 0;
    for (String line : lines) {
      maxWidth = Math.max(maxWidth, client.textRenderer.getWidth(line));
    }

    int padding = 5;
    int iconSize = 16;
    int iconGap = 5;
    int lineHeight = client.textRenderer.fontHeight + 2;
    int textBlockHeight = lineHeight * lines.size();
    int boxWidth = maxWidth + padding * 2 + iconSize + iconGap;
    int boxHeight = Math.max(iconSize, textBlockHeight) + padding * 2;
    int scaledBoxWidth = Math.round(boxWidth * fontScale);
    int scaledBoxHeight = Math.round(boxHeight * fontScale);

    int[] overlayPos =
        resolvePosition(drawContext, scaledBoxWidth, scaledBoxHeight);
    int x = overlayPos[0];
    int y = overlayPos[1];

    drawContext.fill(x, y, x + scaledBoxWidth, y + scaledBoxHeight,
                     backgroundColor);
    drawContext.fill(x, y, x + scaledBoxWidth, y + 1, borderColor);
    drawContext.fill(x, y + scaledBoxHeight - 1, x + scaledBoxWidth,
                     y + scaledBoxHeight, borderColor);
    drawContext.fill(x, y, x + 1, y + scaledBoxHeight, borderColor);
    drawContext.fill(x + scaledBoxWidth - 1, y, x + scaledBoxWidth,
                     y + scaledBoxHeight, borderColor);

    drawContext.getMatrices().push();
    drawContext.getMatrices().translate(x, y, 0);
    drawContext.getMatrices().scale(fontScale, fontScale, 1f);

    int iconY =
        padding +
        Math.max(0, (Math.max(iconSize, textBlockHeight) - iconSize) / 2);
    drawContext.drawItem(displayStack, padding, iconY);

    int textAreaX = padding + iconSize + iconGap;
    int textY =
        padding +
        Math.max(0,
                 (Math.max(iconSize, textBlockHeight) - textBlockHeight) / 2);
    for (int i = 0; i < lines.size(); i++) {
      int lineWidth = client.textRenderer.getWidth(lines.get(i));
      int textX = textAreaX + (maxWidth - lineWidth) / 2;
      drawContext.drawText(client.textRenderer, lines.get(i), textX, textY,
                           textColor, true);
      textY += lineHeight;
    }

    drawContext.getMatrices().pop();
  }

  private static int[] resolvePosition(DrawContext drawContext, int boxWidth,
                                       int boxHeight) {
    int margin = 8;
    int width = drawContext.getScaledWindowWidth();
    int height = drawContext.getScaledWindowHeight();
    OverlayPosition position =
        (OverlayPosition)Configs.OVERLAY_POSITION.getOptionListValue();

    return switch (position) {
      case TOP_LEFT -> new int[] {margin, margin};
      case TOP_CENTER -> new int[] {(width - boxWidth) / 2, margin};
      case TOP_RIGHT -> new int[] {width - boxWidth - margin, margin};
      case BOTTOM_LEFT -> new int[] {margin, height - boxHeight - margin};
      case BOTTOM_CENTER ->
        new int[] {(width - boxWidth) / 2, height - boxHeight - margin};
      case BOTTOM_RIGHT ->
        new int[] {width - boxWidth - margin, height - boxHeight - margin};
    };
  }

  private static int withFullAlpha(int color) {
    return (0xFF << 24) | (color & 0x00FFFFFF);
  }

  private static int withAlpha(int color, int alpha) {
    int clampedAlpha = Math.max(0, Math.min(255, alpha));
    return (clampedAlpha << 24) | (color & 0x00FFFFFF);
  }

  private static String getTargetStateSummary(BlockState state) {
    List<String> parts = new ArrayList<>();
    addProperty(parts, "facing", state, Properties.HORIZONTAL_FACING);
    addProperty(parts, "half", state, Properties.BLOCK_HALF);
    addProperty(parts, "doorHalf", state, Properties.DOUBLE_BLOCK_HALF);
    addProperty(parts, "shape", state, Properties.STAIR_SHAPE);
    addProperty(parts, "hinge", state, Properties.DOOR_HINGE);
    addProperty(parts, "open", state, Properties.OPEN);
    addProperty(parts, "powered", state, Properties.POWERED);
    addProperty(parts, "waterlogged", state, Properties.WATERLOGGED);

    return String.join(", ", parts);
  }

  private static <T extends Comparable<T>> void
  addProperty(List<String> parts, String label, BlockState state,
              Property<T> property) {
    if (!state.contains(property)) {
      return;
    }

    T value = state.get(property);
    String valueString = String.valueOf(value).toLowerCase(Locale.ROOT);
    parts.add(label + "=" + valueString);
  }
}
