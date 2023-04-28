package me.chell.blackout.mixin;

import me.chell.blackout.impl.features.client.FovSlider;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    public void getPositionOffset(CallbackInfoReturnable<SimpleOption<Integer>> cir) {
        if(FovSlider.INSTANCE.getMainSetting().getValue()) cir.setReturnValue(FovSlider.INSTANCE.getSetting());
    }

}
