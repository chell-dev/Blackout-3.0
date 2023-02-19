package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player

class Bhop: ToggleFeature("Bhop", Category.Movement, false) {

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if((player.forwardSpeed != 0f || player.sidewaysSpeed != 0f) && player.isOnGround && !mc.options.jumpKey.isPressed && !player.isRiding && !player.isTouchingWater && !player.isSubmergedInWater)
            player.jump()
    }

}