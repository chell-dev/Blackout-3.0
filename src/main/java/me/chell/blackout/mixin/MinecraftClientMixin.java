package me.chell.blackout.mixin;

import me.chell.blackout.Blackout;
import me.chell.blackout.api.util.FunctionsKt;
import me.chell.blackout.impl.features.client.WindowTitle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    private WindowTitle titleFeature = new WindowTitle();

    @Inject(method = "<init>", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    public void init(RunArgs args, CallbackInfo ci) {
        new Blackout().init();
    }

    @Inject(method = "getWindowTitle", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void getWindowTitle(CallbackInfoReturnable<String> cir, StringBuilder stringBuilder) {
        if(titleFeature.getMainValue().getValue())
            FunctionsKt.setString(stringBuilder, titleFeature.getTitle());
    }
}
