package me.chell.blackout.mixin;

import me.chell.blackout.api.events.RenderHudEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
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

}
