package me.chell.blackout.api.setting

import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.util.mc
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN

abstract class Bind(var key: InputUtil.Key) {

    open fun setKey(keyCode: Int, type: InputUtil.Type) {
        key = type.createFromCode(keyCode)
    }

    abstract fun onKey(event: InputEvent)

    class Action(key: InputUtil.Key = InputUtil.Type.KEYSYM.createFromCode(GLFW_KEY_UNKNOWN),
                 private val action: () -> Unit): Bind(key) {

        constructor(keyCode: Int, type: InputUtil.Type, action: () -> Unit) : this(type.createFromCode(keyCode), action)

        override fun onKey(event: InputEvent) {
            if(key.code != GLFW_KEY_UNKNOWN && event.key == key && event.action == 1 && mc.currentScreen == null) action()
        }
    }

    class Toggle(key: InputUtil.Key = InputUtil.Type.KEYSYM.createFromCode(GLFW_KEY_UNKNOWN),
                 enabled: Boolean = false, var mode: Mode = Mode.Toggle,
                 private val onEnable: () -> Unit, private val onDisable: () -> Unit): Bind(key) {

        var enabled = enabled
            set(value) {
                if(value) {
                    onEnable()
                    field = value
                } else {
                    field = value
                    onDisable()
                }
            }

        constructor(keyCode: Int, type: InputUtil.Type, enabled: Boolean = false, mode: Mode = Mode.Toggle, onEnable: () -> Unit, onDisable: () -> Unit) : this(type.createFromCode(keyCode), enabled, mode, onEnable, onDisable)

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