package me.chell.blackout.impl.features.player

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.mc
import me.chell.blackout.mixin.accessors.MinecraftClientAccessor

object FastPlace: ToggleFeature("FastPlace", Category.PLayer, false) {

    override var description = "No block place / item use delay"

    private val accessor = mc as MinecraftClientAccessor

    override fun onEnable() {
        EventManager.register(this)
    }

    override fun onDisable() {
        EventManager.unregister(this)
    }

    @EventHandler
    fun onTick(event: PlayerTickEvent) {
        accessor.itemUseCooldown = 0
    }

}