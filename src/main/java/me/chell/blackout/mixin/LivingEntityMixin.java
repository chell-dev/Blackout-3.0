package me.chell.blackout.mixin;

import me.chell.blackout.api.event.EventManager;
import me.chell.blackout.api.events.EntityDamagedEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At("TAIL"))
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            EventManager.INSTANCE.post(new EntityDamagedEvent((LivingEntity)(Entity) this, source, amount));
        }
    }

}
