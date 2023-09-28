package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.entity.LivingEntity

// player damages another LivingEntity
class DamageEntityEvent(val target: LivingEntity, val amount: Float): Event()