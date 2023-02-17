package me.chell.blackout.impl.features.client

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.modName
import me.chell.blackout.api.util.modVersion

/** @see me.chell.blackout.mixin.MinecraftClientMixin */
class WindowTitle: ToggleFeature("Change Window Title", Category.Client, true) {
    val title = "$modName $modVersion"
}