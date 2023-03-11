package me.chell.blackout.mixin;

import me.chell.blackout.api.events.RenderHudEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    public void renderTotem(ItemStack floatingItem, CallbackInfo ci) {
        if(floatingItem.getItem() != Items.TOTEM_OF_UNDYING) return;
        RenderHudEvent.Totem event = new RenderHudEvent.Totem(false);
        GlobalsKt.getEventManager().post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    public void bobViewWhenHurt(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        RenderHudEvent.Hurt event = new RenderHudEvent.Hurt(false);
        GlobalsKt.getEventManager().post(event);
        if(event.getCanceled()) ci.cancel();
    }

}
