package me.chell.blackout.mixin;

import me.chell.blackout.api.events.KeyPressedEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if(window == GlobalsKt.getMc().getWindow().getHandle())
            GlobalsKt.getEventManager().post(new KeyPressedEvent(InputUtil.fromKeyCode(key, scancode), action, modifiers));
    }

}