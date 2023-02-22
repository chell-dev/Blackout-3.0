package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.player
import me.chell.blackout.api.setting.Setting
import net.minecraft.entity.effect.StatusEffects

class Sprint: Feature("Sprint", Category.Movement) {

    override val mainSetting = Setting("Enabled", Bind.Toggle(onEnable = { onEnable() }, onDisable = { onDisable() }))

    private val legit = register(Setting("Legit", true))

    private fun onEnable() {
        eventManager.register(this)
    }

    private fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        player.isSprinting = (player.hungerManager.foodLevel > 6 || player.abilities.allowFlying) &&
            if(legit.value) player.forwardSpeed > 0f
                    && (!player.isTouchingWater || player.isSubmergedInWater)
                    && !player.isUsingItem
                    && !player.hasStatusEffect(StatusEffects.BLINDNESS)
            else player.forwardSpeed != 0f || player.sidewaysSpeed != 0f
    }

}