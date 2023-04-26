package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature

object NoSlow: ToggleFeature("NoSlow", Category.Movement) {

    override var description = "Move at normal speed when you're using an item"

}