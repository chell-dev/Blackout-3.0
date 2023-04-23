package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature

class NoSlow : ToggleFeature("NoSlow", Category.Movement, false) {

    override var description = "Move at normal speed when you're using an item"

    companion object {
        lateinit var instance: NoSlow
    }

    init {
        instance = this
    }

}