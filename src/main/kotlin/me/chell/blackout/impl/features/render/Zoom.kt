package me.chell.blackout.impl.features.render

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.FovEvent
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.events.RenderArmEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleBindFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import kotlin.math.max
import kotlin.math.min

object Zoom: ToggleBindFeature("Zoom", Category.Render) {

    private val base = register(Setting("Multiplier", 0.5, 0.1, 1.0))
    private val scroll = register(Setting("Scroll", true))
    private val cinematic = register(object: Setting<Boolean>("Smooth camera", true) {
        override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {
            if(mainSetting.value.enabled) mc.options.smoothCameraEnabled = newValue
        }
    })
    private val hideHands = register(Setting("Hide hands", true))

    private var multiplier = 0.0

    override fun onEnable() {
        super.onEnable()
        if(cinematic.value) mc.options.smoothCameraEnabled = true
        multiplier = base.value
    }

    override fun onDisable() {
        super.onDisable()
        if(cinematic.value) mc.options.smoothCameraEnabled = false
    }

    @EventHandler
    fun onFov(event: FovEvent) {
        event.fov *= multiplier
    }

    @EventHandler
    fun onScroll(event: InputEvent.Scroll) {
        if(scroll.value) {
            event.canceled = true
            multiplier = min(max(0.01, multiplier + (event.amount * -0.025)), 1.0)
        }
    }

    @EventHandler
    fun onRenderHands(event: RenderArmEvent) {
        if(hideHands.value) event.canceled = true
    }

}