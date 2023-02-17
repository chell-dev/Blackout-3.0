package me.chell.blackout.mixin

import me.chell.blackout.api.events.KeyPressedEvent
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.mc
import net.minecraft.client.Keyboard
import net.minecraft.client.util.InputUtil
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Keyboard::class)
class KeyboardMixin {

    @Inject(method = ["onKey"], at = [At("HEAD")])
    fun onKey(window: Long, key: Int, scancode: Int, action: Int, modifiers: Int, ci: CallbackInfo) {
        if(window == mc.window.handle) eventManager.post(KeyPressedEvent(InputUtil.fromKeyCode(key, scancode), action, modifiers))
    }

}