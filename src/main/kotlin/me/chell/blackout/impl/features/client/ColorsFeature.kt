package me.chell.blackout.impl.features.client

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Color

class ColorsFeature : Feature("Colors", Category.Client) {
    override val mainSetting = Setting("Empty", null)

    val sync = register(Setting("Sync", Color(161, 0, 255)))
    val rainbowSpeed = register(Setting("Rainbow Speed", 1f, 0.1f, 5f))

    companion object {
        lateinit var instance: ColorsFeature
    }

    init {
        instance = this
    }
}