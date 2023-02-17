package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.NoRegister
import me.chell.blackout.api.feature.ToggleFeature

@NoRegister
class Bhop: ToggleFeature("Bhop", Category.Movement, false) {
}