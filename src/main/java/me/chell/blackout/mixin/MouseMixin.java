package me.chell.blackout.mixin;

import me.chell.blackout.api.event.EventManager;
import me.chell.blackout.api.events.InputEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void onKey(long window, int key, int action, int modifiers, CallbackInfo ci) {
        if(window == GlobalsKt.getMc().getWindow().getHandle())
            EventManager.INSTANCE.post(new InputEvent.Mouse(InputUtil.Type.MOUSE.createFromCode(key), action, modifiers));
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, double horizontal, double vertical, CallbackInfo ci) {
        if(window == GlobalsKt.getMc().getWindow().getHandle()) {
            InputEvent.Scroll event = new InputEvent.Scroll(vertical, false);
            EventManager.INSTANCE.post(event);
            if(event.getCanceled()) ci.cancel();
        }
    }

}
