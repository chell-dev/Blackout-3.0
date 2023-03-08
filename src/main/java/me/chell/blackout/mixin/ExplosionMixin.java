package me.chell.blackout.mixin;

import me.chell.blackout.api.events.PlayerKnockbackEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow @Final private Map<PlayerEntity, Vec3d> affectedPlayers;

    @Inject(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void knockback(CallbackInfo ci, Set set, int i, float q, int k, int l, int r, int s, int t, int u, List list, Vec3d vec3d, int v, Entity entity, double w, double x, double y, double z, double aa, double ab, double ac, double ad) {
        ClientPlayerEntity player = GlobalsKt.getPlayer();

        if(entity.getUuid() == player.getUuid()) {
            PlayerKnockbackEvent event = new PlayerKnockbackEvent();
            GlobalsKt.getEventManager().post(event);

            if(event.getCanceled()) {
                ci.cancel();
                if(!player.isSpectator() && !player.isCreative() && !player.getAbilities().flying) affectedPlayers.put(player, new Vec3d(x * ac, y * ac, z * ac));
            }
        }
    }

}
