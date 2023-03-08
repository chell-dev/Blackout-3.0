package me.chell.blackout.mixin;

import me.chell.blackout.api.events.PlayerKnockbackEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
    public void knockback(double strength, double x, double z, CallbackInfo ci) {
        if(((LivingEntity)(Object)this).getUuid() == GlobalsKt.getPlayer().getUuid()) {
            PlayerKnockbackEvent event = new PlayerKnockbackEvent();
            GlobalsKt.getEventManager().post(event);
            if(event.getCanceled()) ci.cancel();
        }
    }

    @Inject(method = "getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D", at = @At("RETURN"), cancellable = true)
    public void knockbackAttribute(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
        if(attribute == EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE &&  ((LivingEntity)(Object)this).getUuid() == GlobalsKt.getPlayer().getUuid()) {
            PlayerKnockbackEvent event = new PlayerKnockbackEvent();
            GlobalsKt.getEventManager().post(event);
            if(event.getCanceled()) cir.setReturnValue(1.0);
        }
    }

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
}
