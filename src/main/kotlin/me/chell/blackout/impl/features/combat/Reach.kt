package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting

/** @see me.chell.blackout.mixin.ClientPlayerInteractionManagerMixin */
class Reach: ToggleFeature("Reach", Category.Combat, false) {
    val range = register(Setting("Range", 6.0f, 0.0f, 12.0f))

    companion object {
        lateinit var instance: Reach
    }

    init {
        instance = this
    }
}