package me.chell.blackout.impl.features.client

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.defaultConfig
import me.chell.blackout.api.util.readFeatures
import me.chell.blackout.api.util.writeFeatures
import java.io.File

object ConfigFeature: Feature("Config", Category.Client) {

    override val mainSetting = Setting("File", File(defaultConfig))

    private val save = register(Setting("Save", Runnable { writeFeatures() }))
    private val load = register(Setting("Load", false))
    private val confirmLoad = register(Setting("Confirm Load", Runnable { readFeatures(); load.value = false }, level = 2) {load.value})

}