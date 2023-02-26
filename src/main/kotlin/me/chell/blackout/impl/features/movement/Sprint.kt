package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.player
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Text

class Sprint: Feature("Sprint", Category.Movement) {

    override val mainSetting = Setting("Enabled", Bind.Toggle(onEnable = { onEnable() }, onDisable = { onDisable() }))

    private val mode = register(Setting("Mode", Mode.Speed))

    private fun onEnable() {
        eventManager.register(this)
    }

    private fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        // todo use 'when'
        if(mode.value == Mode.Legit) {
            if((player.hungerManager.foodLevel > 6 || player.abilities.allowFlying) &&
                    player.forwardSpeed > 0f
                    && (!player.isTouchingWater || player.isSubmergedInWater)
                    && !player.isUsingItem
                    && !player.hasStatusEffect(StatusEffects.BLINDNESS))
                player.isSprinting = true
        } else if(mode.value == Mode.Rage) {
            if(player.forwardSpeed != 0f || player.sidewaysSpeed != 0f) player.isSprinting = true
        } else if(mode.value == Mode.Speed) {
            if(mc.options.forwardKey.isPressed) {
                player.forwardSpeed = 1f
                player.isSprinting = true
                player.airStrafingSpeed = 1f
            } else if(mc.options.backKey.isPressed) {
                player.forwardSpeed = -1f
                player.isSprinting = true
                player.airStrafingSpeed = 1f
            } else {
                player.forwardSpeed = 0f
                player.isSprinting = false
                player.airStrafingSpeed = 0.02f
            }

            if(mc.options.rightKey.isPressed) {
                player.sidewaysSpeed = 1f
                player.isSprinting = true
                player.airStrafingSpeed = 1f
            } else if(mc.options.leftKey.isPressed) {
                player.sidewaysSpeed = -1f
                player.isSprinting = true
                player.airStrafingSpeed = 1f
            } else {
                player.sidewaysSpeed = 0f
                player.isSprinting = false
                player.airStrafingSpeed = 0.02f
            }
        }
    }

    enum class Mode {
        Legit, Rage, Speed
    }

}