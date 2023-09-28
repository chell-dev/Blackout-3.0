package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource

// any LivingEntity takes damage
class EntityDamagedEvent(val entity: LivingEntity, val damageSource: DamageSource, val damageAmount: Float): Event()