package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleBindFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.player
import net.minecraft.entity.effect.StatusEffects

object Sprint: ToggleBindFeature("Sprint", Category.Movement) {

    override var description = "Sprint automatically"

    private val mode = Setting("Mode", Mode.Rage)

    override fun onEnable() {
        EventManager.register(this)
    }

    override fun onDisable() {
        EventManager.unregister(this)
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if(mode.value == Mode.Legit) {
            if((player.hungerManager.foodLevel > 6 || player.abilities.allowFlying) &&
                    player.forwardSpeed > 0f
                    && (!player.isTouchingWater || player.isSubmergedInWater)
                    && !player.isUsingItem
                    && !player.hasStatusEffect(StatusEffects.BLINDNESS))
                player.isSprinting = true
        } else {
            if(player.forwardSpeed != 0f || player.sidewaysSpeed != 0f) player.isSprinting = true
        }
    }

    enum class Mode {
        Legit, Rage
    }

}