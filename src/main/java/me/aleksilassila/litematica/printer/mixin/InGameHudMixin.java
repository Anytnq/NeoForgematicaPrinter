package me.aleksilassila.litematica.printer.mixin;

import me.aleksilassila.litematica.printer.integration.overlay.SchematicOverlayRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void renderSchematicOverlay(DrawContext drawContext, RenderTickCounter tickCounter, CallbackInfo ci) {
        SchematicOverlayRenderer.render(drawContext);
    }
}
