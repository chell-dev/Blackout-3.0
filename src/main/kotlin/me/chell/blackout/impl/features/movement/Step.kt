package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc

class Step: Feature("Step", Category.Movement) {
    override val mainSetting = Setting("Enabled", Bind.Toggle(onEnable = {onEnable()}, onDisable = {onDisable()}))

    private val height = Setting("Height", 2.1f)

    private fun onEnable() {
        mc.player?.stepHeight = height.value
    }

    private fun onDisable() {
        mc.player?.stepHeight = 0.6f
    }
}