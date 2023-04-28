package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerKnockbackEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature

object Velocity: ToggleFeature("Velocity", Category.Movement) {

    override var description = "Anti knockback"

    @EventHandler
    fun onKnockback(event: PlayerKnockbackEvent) {
        event.canceled = true
    }

    //@EventHandler
    //fun onTick(event: PlayerTickEvent) {
    //    player.knockbackVelocity = 0.0f
    //}


}