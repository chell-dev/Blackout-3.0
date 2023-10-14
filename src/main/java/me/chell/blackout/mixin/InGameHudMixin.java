package me.chell.blackout.mixin;

import me.chell.blackout.api.event.EventManager;
import me.chell.blackout.api.events.RenderHudEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    public void postRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        EventManager.INSTANCE.post(new RenderHudEvent.Post(context, tickDelta));
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    public void renderOverlay(DrawContext context, Identifier texture, float opacity, CallbackInfo ci) {
        RenderHudEvent.Overlay event = new RenderHudEvent.Overlay(texture, false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    public void renderOverlay(DrawContext context, CallbackInfo ci) {
        RenderHudEvent.Tooltip event = new RenderHudEvent.Tooltip(false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    public void renderOverlay(DrawContext context, float nauseaStrength, CallbackInfo ci) {
        RenderHudEvent.Portal event = new RenderHudEvent.Portal(false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void renderCrosshair(DrawContext context, CallbackInfo ci) {
        RenderHudEvent.Crosshair event = new RenderHudEvent.Crosshair(context, false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

}
