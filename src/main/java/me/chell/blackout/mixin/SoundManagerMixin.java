package me.chell.blackout.mixin;

import me.chell.blackout.api.event.EventManager;
import me.chell.blackout.api.events.SoundEvent;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"))
    public void play(SoundInstance sound, CallbackInfo ci) {
        EventManager.INSTANCE.post(new SoundEvent(sound));
    }

}
