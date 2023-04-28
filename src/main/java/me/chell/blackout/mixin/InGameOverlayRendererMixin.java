package me.chell.blackout.mixin;

import me.chell.blackout.api.event.EventManager;
import me.chell.blackout.api.events.RenderHudEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

    @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
    private static void renderInWallOverlay(Sprite sprite, MatrixStack matrices, CallbackInfo ci) {
        RenderHudEvent.InWall event = new RenderHudEvent.InWall(false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void renderFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        RenderHudEvent.OnFire event = new RenderHudEvent.OnFire(false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void renderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        RenderHudEvent.Underwater event = new RenderHudEvent.Underwater(false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

}
