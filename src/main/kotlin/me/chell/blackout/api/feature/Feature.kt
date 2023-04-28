package me.chell.blackout.api.feature

import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Description
import me.chell.blackout.api.util.modId
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

abstract class Feature(val name: String, val category: Category): Description {
    abstract val mainSetting: Setting<*>
    val settings = mutableListOf<Setting<*>>()

    fun <T> register(setting: Setting<T>): Setting<T> {
        settings.add(setting)
        return setting
    }

    fun getSettingByName(name: String): Setting<*>? {
        for(setting in settings) {
            if(setting.name == name) return setting
        }
        return null
    }

    override var description = "No description."
}

@NoRegister
abstract class ToggleFeature(name: String, category: Category, enabled: Boolean = false): Feature(name, category) {

    override val mainSetting: Setting<Boolean> = object: Setting<Boolean>("Enabled", enabled) {
        override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {
            if(newValue) onEnable()
            else onDisable()
        }
    }

    open fun onEnable() {
        EventManager.register(this)
    }
    open fun onDisable() {
        EventManager.unregister(this)
    }
}

@NoRegister
abstract class ToggleBindFeature(name: String, category: Category, keyCode: Int = GLFW.GLFW_KEY_UNKNOWN, enabled: Boolean = false, mode: Bind.Toggle.Mode = Bind.Toggle.Mode.Toggle): Feature(name, category) {

    override val mainSetting = Setting("Bind", Bind.Toggle(InputUtil.fromKeyCode(keyCode, -1), enabled, mode, {onEnable()}, {onDisable()}))

    open fun onEnable() {
        EventManager.register(this)
    }
    open fun onDisable() {
        EventManager.unregister(this)
    }
}

@NoRegister
abstract class ActionBindFeature(name: String, category: Category, keyCode: Int = GLFW.GLFW_KEY_UNKNOWN): Feature(name, category) {

    override val mainSetting = Setting("Bind", Bind.Action(InputUtil.fromKeyCode(keyCode, -1)) { activate() })

    abstract fun activate()
}

annotation class NoRegister

enum class Category(val icon: Identifier): Description {
    Combat(Identifier(modId, "textures/gui/categories/combat.png")) { override var description = "Combat"},
    Render(Identifier(modId, "textures/gui/categories/render.png")) { override var description = "Render"},
    PLayer(Identifier(modId, "textures/gui/categories/player.png")) { override var description = "Player"},
    Movement(Identifier(modId, "textures/gui/categories/movement.png")) { override var description = "Movement"},
    Misc(Identifier(modId, "textures/gui/categories/misc.png")) { override var description = "Misc"},
    Client(Identifier(modId, "textures/gui/categories/client.png")) { override var description = "Client"},
    Hud(Identifier(modId, "textures/gui/categories/hud.png")) { override var description = "HUD"}
}