package me.chell.blackout.impl.features.client

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.defaultConfig
import me.chell.blackout.api.util.readFeatures
import me.chell.blackout.api.util.writeFeatures
import java.io.File

class ConfigFeature: Feature("Config", Category.Client) {

    override val mainSetting = Setting("File", File(defaultConfig))

    val save = register(Setting("Save", Runnable { writeFeatures(mainSetting.value.absolutePath) }))
    val load = register(Setting("Load", false))
    val confirmLoad = register(Setting("Confirm Load", Runnable { readFeatures(mainSetting.value.absolutePath) }))

    init {
        mainSetting.value.parentFile.mkdirs()
        mainSetting.value.createNewFile()
    }

}