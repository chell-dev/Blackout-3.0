package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature

class NoSlow: ToggleFeature("NoSlow", Category.Movement, false) {

    companion object {
        lateinit var instance: NoSlow
    }

    init {
        instance = this
    }

}