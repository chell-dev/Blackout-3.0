package me.chell.blackout.mixin;

import me.chell.blackout.Blackout;
import me.chell.blackout.api.events.ServerEvent;
import me.chell.blackout.api.util.FunctionsKt;
import me.chell.blackout.api.util.GlobalsKt;
import me.chell.blackout.impl.features.client.WindowTitle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    public void init(RunArgs args, CallbackInfo ci) {
        new Blackout().init();
    }

    @Inject(method = "getWindowTitle", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void getWindowTitle(CallbackInfoReturnable<String> cir, StringBuilder stringBuilder) {
        if(WindowTitle.instance != null && WindowTitle.instance.getMainSetting().getValue()) {
            FunctionsKt.setString(stringBuilder, WindowTitle.instance.getTitle());
        }
    }

    @Inject(method = "getWindowTitle", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", ordinal = 4, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void singlePlayer(CallbackInfoReturnable<String> cir, StringBuilder stringBuilder) {
        if(Blackout.instance != null)
            GlobalsKt.getEventManager().post(new ServerEvent.SinglePlayer());
    }

    @Inject(method = "getWindowTitle", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", ordinal = 5, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void realms(CallbackInfoReturnable<String> cir, StringBuilder stringBuilder) {
        if(Blackout.instance != null)
            GlobalsKt.getEventManager().post(new ServerEvent.Realms());
    }

    @Inject(method = "getWindowTitle", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", ordinal = 6, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void lan(CallbackInfoReturnable<String> cir, StringBuilder stringBuilder) {
        if(Blackout.instance != null)
            GlobalsKt.getEventManager().post(new ServerEvent.Lan());
    }

    @Inject(method = "getWindowTitle", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", ordinal = 7, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void multiPlayer(CallbackInfoReturnable<String> cir, StringBuilder stringBuilder) {
        if(Blackout.instance != null)
            GlobalsKt.getEventManager().post(new ServerEvent.MultiPlayer());
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    public void disconnect(Screen screen, CallbackInfo ci) {
        GlobalsKt.getEventManager().post(new ServerEvent.Disconnect());
    }

}
