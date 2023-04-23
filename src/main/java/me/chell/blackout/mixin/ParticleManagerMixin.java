package me.chell.blackout.mixin;

import me.chell.blackout.api.events.ParticleEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
    public void addParticle(Particle particle, CallbackInfo ci) {
        ParticleEvent event = new ParticleEvent(particle, false);
        GlobalsKt.getEventManager().post(event);
        if (event.getCanceled()) ci.cancel();
    }

}
