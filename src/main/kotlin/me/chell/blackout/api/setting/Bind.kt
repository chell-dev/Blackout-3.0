package me.chell.blackout.api.setting

import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.BindEvent
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.util.mc
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN

abstract class Bind(val name: String, var key: InputUtil.Key) {

    open fun setKey(keyCode: Int, type: InputUtil.Type) {
        key = type.createFromCode(keyCode)
    }

    abstract fun onKey(event: InputEvent)

    class Action(name: String, key: InputUtil.Key = InputUtil.Type.KEYSYM.createFromCode(GLFW_KEY_UNKNOWN),
                 private val action: () -> Unit): Bind(name, key) {

        constructor(name: String, keyCode: Int, type: InputUtil.Type, action: () -> Unit) : this(name, type.createFromCode(keyCode), action)

        override fun onKey(event: InputEvent) {
            if(key.code != GLFW_KEY_UNKNOWN && event.key == key && event.action == 1 && mc.currentScreen == null) {
                EventManager.post(BindEvent(this))
                action()
            }
        }
    }

    class Toggle(name: String, key: InputUtil.Key = InputUtil.Type.KEYSYM.createFromCode(GLFW_KEY_UNKNOWN),
                 enabled: Boolean = false, var mode: Mode = Mode.Toggle,
                 private val onEnable: () -> Unit, private val onDisable: () -> Unit): Bind(name, key) {

        var enabled = enabled
        set(value) {
            if(field == value) return
            EventManager.post(BindEvent(this))
            if(value) {
                onEnable()
                field = value
            } else {
                field = value
                onDisable()
            }
        }

        constructor(name: String, keyCode: Int, type: InputUtil.Type, enabled: Boolean = false, mode: Mode = Mode.Toggle, onEnable: () -> Unit, onDisable: () -> Unit) : this(name, type.createFromCode(keyCode), enabled, mode, onEnable, onDisable)

        override fun onKey(event: InputEvent) {
            when(event.action) {
                1 -> {
                    if(key.code != GLFW_KEY_UNKNOWN && event.key == key
                        && mc.currentScreen == null) enabled = !enabled
                }
                0 -> {
                    if(key.code != GLFW_KEY_UNKNOWN && event.key == key
                        && mode == Mode.Hold) enabled = !enabled
                }
            }
        }

        enum class Mode {
            Toggle, Hold
        }
    }

}