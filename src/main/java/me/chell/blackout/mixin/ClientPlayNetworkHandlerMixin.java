package me.chell.blackout.mixin;

import me.chell.blackout.api.events.PlayerKnockbackEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"), cancellable = true)
    public void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
        PlayerKnockbackEvent event = new PlayerKnockbackEvent();
        GlobalsKt.getEventManager().post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocityClient(DDD)V"), cancellable = true)
    public void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        if(packet.getId() != GlobalsKt.getPlayer().getId()) return;

        PlayerKnockbackEvent event = new PlayerKnockbackEvent();
        GlobalsKt.getEventManager().post(event);
        if(event.getCanceled()) ci.cancel();
    }

}
