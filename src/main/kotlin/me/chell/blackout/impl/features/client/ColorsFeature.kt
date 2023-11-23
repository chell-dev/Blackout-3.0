package me.chell.blackout.impl.features.client

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Color

object ColorsFeature: Feature("Colors", Category.Client) {

    override val mainSetting = Setting("Empty", null)

    val sync = Setting("Sync", Color(161, 0, 255))
    val rainbowSpeed = Setting("Rainbow Speed", 1f, 0.1f, 5f)

}