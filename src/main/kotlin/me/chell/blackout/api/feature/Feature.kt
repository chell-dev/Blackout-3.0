package me.chell.blackout.api.feature

import me.chell.blackout.api.util.modId
import me.chell.blackout.api.value.Value
import net.minecraft.util.Identifier

abstract class Feature(val name: String, val category: Category) {
    abstract val mainValue: Value<*>
}

@NoRegister
abstract class ToggleFeature(name: String, category: Category, enabled: Boolean): Feature(name, category) {

    override val mainValue: Value<Boolean> = object: Value<Boolean>("Enabled", enabled) {
        override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {
            if(newValue) onEnable()
            else onDisable()
        }
    }

    open fun onEnable() {}
    open fun onDisable() {}
}

annotation class NoRegister

enum class Category(icon: Identifier) {
    Combat(Identifier(modId, "ui/combat.png")),
    Render(Identifier(modId, "ui/render.png")),
    PLayer(Identifier(modId, "ui/player.png")),
    Movement(Identifier(modId, "ui/movement.png")),
    Misc(Identifier(modId, "ui/misc.png")),
    Client(Identifier(modId, "ui/client.png"))
}