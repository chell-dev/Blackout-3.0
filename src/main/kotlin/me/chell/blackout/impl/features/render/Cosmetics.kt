package me.chell.blackout.impl.features.render

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.isFriend
import me.chell.blackout.api.util.player

object Cosmetics: ToggleFeature("Cosmetics", Category.Render) {

    override var description = "Client-side player cosmetics"

    private val cape = Setting("Cape", true)
    private val ears = Setting("Ears", false)
    private val mode = Setting("Mode", Mode.Self)

    fun getCape(name: String) = get(name) && mainSetting.value && cape.value
    fun getEars(name: String) = get(name) && mainSetting.value && ears.value

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