package me.aleksilassila.litematica.printer.integration.overlay;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum OverlayPosition implements IConfigOptionListEntry {
    TOP_LEFT("top_left", "litematica-printer.label.overlayPosition.topLeft"),
    TOP_CENTER("top_center", "litematica-printer.label.overlayPosition.topCenter"),
    TOP_RIGHT("top_right", "litematica-printer.label.overlayPosition.topRight"),
    BOTTOM_LEFT("bottom_left", "litematica-printer.label.overlayPosition.bottomLeft"),
    BOTTOM_CENTER("bottom_center", "litematica-printer.label.overlayPosition.bottomCenter"),
    BOTTOM_RIGHT("bottom_right", "litematica-printer.label.overlayPosition.bottomRight");

    private final String configString;
    private final String translationKey;

    OverlayPosition(String configString, String translationKey) {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public String getStringValue() {
        return this.configString;
    }

    @Override
    public String getDisplayName() {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();

        if (forward) {
            id = (id + 1) % values().length;
        } else {
            id = (id - 1 + values().length) % values().length;
        }

        return values()[id];
    }

    @Override
    public OverlayPosition fromString(String value) {
        for (OverlayPosition position : values()) {
            if (position.configString.equalsIgnoreCase(value)) {
                return position;
            }
        }

        return TOP_CENTER;
    }
}
