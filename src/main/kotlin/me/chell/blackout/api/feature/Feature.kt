package me.chell.blackout.api.feature

import me.chell.blackout.api.util.modId
import me.chell.blackout.api.value.Setting
import net.minecraft.util.Identifier

abstract class Feature(val name: String, val category: Category) {
    abstract val mainSetting: Setting<*>
    val settings = mutableListOf<Setting<*>>()

    fun <T> register(setting: Setting<T>): Setting<T> {
        settings.add(setting)
        return setting
    }
}

@NoRegister
abstract class ToggleFeature(name: String, category: Category, enabled: Boolean): Feature(name, category) {

    override val mainSetting: Setting<Boolean> = object: Setting<Boolean>("Enabled", enabled) {
        override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {
            if(newValue) onEnable()
            else onDisable()
        }
    }

    open fun onEnable() {}
    open fun onDisable() {}
}

annotation class NoRegister

enum class Category(val icon: Identifier, val focusedIcon: Identifier) {
    Combat(Identifier(modId, "textures/gui/categories/combat.png"), Identifier(modId, "textures/gui/categories/combat_focused.png")),
    Render(Identifier(modId, "textures/gui/categories/render.png"), Identifier(modId, "textures/gui/categories/render_focused.png")),
    PLayer(Identifier(modId, "textures/gui/categories/player.png"), Identifier(modId, "textures/gui/categories/player_focused.png")),
    Movement(Identifier(modId, "textures/gui/categories/movement.png"), Identifier(modId, "textures/gui/categories/movement_focused.png")),
    Misc(Identifier(modId, "textures/gui/categories/misc.png"), Identifier(modId, "textures/gui/categories/misc_focused.png")),
    Client(Identifier(modId, "textures/gui/categories/client.png"), Identifier(modId, "textures/gui/categories/client_focused.png"))
}