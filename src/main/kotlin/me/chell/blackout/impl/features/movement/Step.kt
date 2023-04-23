package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc

class Step : Feature("Step", Category.Movement) {

    override var description = "Increase your step height"

    override val mainSetting = Setting("Enabled", Bind.Toggle(onEnable = { onEnable() }, onDisable = { onDisable() }))

    private val height = register(Setting("Height", 2.2f, 0.6f, 3f))

    private fun onEnable() {
        mc.player?.stepHeight = height.value
    }

    private fun onDisable() {
        mc.player?.stepHeight = 0.6f
    }
}