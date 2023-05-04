package me.chell.blackout.mixin;

import me.chell.blackout.api.event.EventManager;
import me.chell.blackout.api.events.FovEvent;
import me.chell.blackout.api.events.RenderArmEvent;
import me.chell.blackout.api.events.RenderHudEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    public void renderTotem(ItemStack floatingItem, CallbackInfo ci) {
        if(floatingItem.getItem() != Items.TOTEM_OF_UNDYING) return;
        RenderHudEvent.Totem event = new RenderHudEvent.Totem(false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    public void bobViewWhenHurt(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        RenderHudEvent.Hurt event = new RenderHudEvent.Hurt(false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "getFov", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir, double d) {
        if(changingFov) {
            FovEvent event = new FovEvent(d);
            EventManager.INSTANCE.post(event);
            cir.setReturnValue(event.getFov());
        } else {
            RenderArmEvent event = new RenderArmEvent(RenderArmEvent.Type.Fov, new MatrixStack(), 0f, false, d);
            EventManager.INSTANCE.post(event);
            cir.setReturnValue(event.getFov());
        }
    }

}
