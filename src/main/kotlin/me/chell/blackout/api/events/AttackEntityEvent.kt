package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.entity.Entity

// player attacks another Entity
abstract class AttackEntityEvent(val target: Entity): Event() {
    class Pre(target: Entity, var canceled: Boolean): AttackEntityEvent(target)
    class Post(target: Entity): AttackEntityEvent(target)
}