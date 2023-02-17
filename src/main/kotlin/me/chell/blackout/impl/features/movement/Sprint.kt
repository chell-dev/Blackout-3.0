package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.player
import me.chell.blackout.api.value.Value
import net.minecraft.entity.effect.StatusEffects

class Sprint: ToggleFeature("Sprint", Category.Movement, false) {

    private val legit = Value("Legit", false)

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        player.isSprinting =

            (player.hungerManager.foodLevel > 6 || player.abilities.allowFlying) &&

            if(legit.value) player.forwardSpeed > 0f
                    && !player.isSprinting
                    && (!player.isTouchingWater || player.isSubmergedInWater)
                    && !player.isUsingItem
                    && !player.hasStatusEffect(StatusEffects.BLINDNESS)

            else player.forwardSpeed != 0f || player.sidewaysSpeed != 0f
    }

}