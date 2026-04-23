package me.aleksilassila.litematica.printer;

import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.MinecraftVersion;

public class PrinterReference {
  public static final String MOD_ID = "neoforgematica_printer";
  public static final String MOD_KEY = "litematica-printer";
  public static final String MOD_NAME = "NeoForgematica Printer";
  public static final String MOD_VERSION =
      StringUtils.getModVersionString(MOD_ID);
  public static final String MC_VERSION = MinecraftVersion.CURRENT.getName();
  public static final String MOD_STRING =
      MOD_ID + "-" + MC_VERSION + "-" + MOD_VERSION;
}
