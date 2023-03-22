package me.chell.blackout.impl.features.misc

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature

class AutoRespawn: ToggleFeature("Auto Respawn", Category.Misc, false) {

    companion object {
        lateinit var instance: AutoRespawn
    }

    init {
        instance = this
    }

}