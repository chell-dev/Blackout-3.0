package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting

/** @see me.chell.blackout.mixin.ClientPlayerInteractionManagerMixin */
object Reach: ToggleFeature("Reach", Category.Combat) {

    override var description = "Increase your reach distance"

    val range = register(Setting("Range", 6.0f, 0.0f, 12.0f))
}