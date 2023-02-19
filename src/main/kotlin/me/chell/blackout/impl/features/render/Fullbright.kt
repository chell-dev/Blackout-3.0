package me.chell.blackout.impl.features.render

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature

class Fullbright: ToggleFeature("Fullbright", Category.Render, false) {

    companion object {
        lateinit var instance: Fullbright
    }

    init {
        instance = this
    }
}