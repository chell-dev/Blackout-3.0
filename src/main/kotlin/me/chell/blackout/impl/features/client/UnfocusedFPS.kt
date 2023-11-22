package me.chell.blackout.impl.features.client

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.WindowFocusChangedEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc

object UnfocusedFPS: ToggleFeature("Unfocused FPS Limit", Category.Client) {

    private val limit = register(Setting("Limit", 10, 1, 60))

    @EventHandler
    fun onFocusChanged(event: WindowFocusChangedEvent) {
         mc.window.framerateLimit = if(event.focused) mc.options.maxFps.value else limit.value
    }

}