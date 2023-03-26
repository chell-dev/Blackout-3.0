package me.chell.blackout.impl.features.render

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.isFriend
import me.chell.blackout.api.util.player

class Cosmetics: ToggleFeature("Cosmetics", Category.Render, false) {
    private val cape = register(Setting("Cape", true))
    private val ears = register(Setting("Ears", false))
    private val mode = register(Setting("Mode", Mode.Self))

    companion object {
        private lateinit var instance: Cosmetics

        fun getCape(name: String) = instance.get(name) && instance.mainSetting.value && instance.cape.value
        fun getEars(name: String) = instance.get(name) && instance.mainSetting.value && instance.ears.value

    }

    init {
        instance = this
    }

    private fun get(name: String): Boolean {
        return when(mode.value) {
            Mode.Self -> name == player.name.string
            Mode.Friends -> name == player.name.string || isFriend(name)
            Mode.Everyone -> true
        }
    }

    enum class Mode {
        Self, Friends, Everyone
    }

}