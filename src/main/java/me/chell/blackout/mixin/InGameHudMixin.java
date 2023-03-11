package me.chell.blackout.mixin;

import me.chell.blackout.api.events.RenderHudEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    public void postRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        GlobalsKt.getEventManager().post(new RenderHudEvent.Post(matrices, tickDelta));
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    public void renderOverlay(Identifier texture, float opacity, CallbackInfo ci) {
        RenderHudEvent.Overlay event = new RenderHudEvent.Overlay(texture, false);
        GlobalsKt.getEventManager().post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    public void renderOverlay(MatrixStack matrices, CallbackInfo ci) {
        RenderHudEvent.Tooltip event = new RenderHudEvent.Tooltip(false);
        GlobalsKt.getEventManager().post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    public void renderOverlay(float nauseaStrength, CallbackInfo ci) {
        RenderHudEvent.Portal event = new RenderHudEvent.Portal(false);
        GlobalsKt.getEventManager().post(event);
        if(event.getCanceled()) ci.cancel();
    }

}
