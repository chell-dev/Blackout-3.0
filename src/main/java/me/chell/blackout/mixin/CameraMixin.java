package me.chell.blackout.mixin;

import me.chell.blackout.impl.features.render.FirstPersonBody;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true)
    public void isThirdPerson(CallbackInfoReturnable<Boolean> cir) {
        if(FirstPersonBody.Companion.isActive())
            cir.setReturnValue(true);
    }

}
