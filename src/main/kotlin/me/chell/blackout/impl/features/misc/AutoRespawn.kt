package me.chell.blackout.impl.features.misc

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature

object AutoRespawn: ToggleFeature("Auto Respawn", Category.Misc) {

    override var description = "Don't open the death screen when you die"

}