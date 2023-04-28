package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleBindFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc

object Step: ToggleBindFeature("Step", Category.Movement) {

    override var description = "Increase your step height"

    private val height = register(Setting("Height", 2.2f, 0.6f, 3f))

    override fun onEnable() {
        mc.player?.stepHeight = height.value
    }

    override fun onDisable() {
        mc.player?.stepHeight = 0.6f
    }
}