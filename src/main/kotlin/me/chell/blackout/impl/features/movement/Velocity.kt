package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerKnockbackEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.eventManager

object Velocity: ToggleFeature("Velocity", Category.Movement, false) {

    override var description = "Anti knockback"

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onKnockback(event: PlayerKnockbackEvent) {
        event.canceled = true
    }

    //@EventHandler
    //fun onTick(event: PlayerTickEvent) {
    //    player.knockbackVelocity = 0.0f
    //}


}