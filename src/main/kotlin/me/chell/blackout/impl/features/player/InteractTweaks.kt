package me.chell.blackout.impl.features.player

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleBindFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import me.chell.blackout.mixin.accessors.ClientPlayerInteractionManagerAccessor
import me.chell.blackout.mixin.accessors.MinecraftClientAccessor

object InteractTweaks: ToggleBindFeature("Interaction Tweaks", Category.PLayer) {

    private val placeDelay = register(Setting("No Place/Use Delay", false))
    private val breakDelay = register(Setting("No Break Delay", false))
    private val attackDelay = register(Setting("No Attack Delay", false))
    private val multiTask = register(Setting("Multi Task", false)) // MinecraftClientMixin.isUsingItem()
    private val stickyBreak = register(Setting("Sticky Block Breaking", false)) // ClientPlayerInteractionManager.cancelBlockBreaking()

    @EventHandler
    fun onTick(event: PlayerTickEvent) {
        if(placeDelay.value) (mc as MinecraftClientAccessor).itemUseCooldown = 0
        if(attackDelay.value) (mc as MinecraftClientAccessor).setAttackCooldown(0)
        if(breakDelay.value) (interactionManager as ClientPlayerInteractionManagerAccessor).setBlockBreakingCooldown(0)
    }

    @EventHandler
    fun onInput(event: InputEvent) {
        if(event.action == 1
            && (mc.options.attackKey.matchesMouse(event.key.code) || mc.options.attackKey.matchesKey(event.key.code, -1))
            && mc.player != null && multiTask.value && player.isUsingItem) {
                if(mc.targetedEntity != null) player.attackEntity(mc.targetedEntity!!, false)
                else player.attackAir()
        }
    }

    fun multiTaskEnabled(): Boolean = mainSetting.value.enabled && multiTask.value
    fun stickyBreakEnabled(): Boolean = mainSetting.value.enabled && stickyBreak.value

}