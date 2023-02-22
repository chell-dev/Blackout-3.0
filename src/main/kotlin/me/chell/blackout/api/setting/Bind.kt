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
                 var enabled: Boolean = false, var mode: Mode = Mode.TOGGLE,
                 private val onEnable: () -> Unit, private val onDisable: () -> Unit): Bind(key) {

        constructor(keyCode: Int, type: InputUtil.Type, enabled: Boolean = false, mode: Mode = Mode.TOGGLE, onEnable: () -> Unit, onDisable: () -> Unit) : this(type.createFromCode(keyCode), enabled, mode, onEnable, onDisable)

        override fun onKey(event: InputEvent) {
            when(event.action) {
                1 -> {
                    if(key.code != GLFW_KEY_UNKNOWN && event.key == key
                        && mc.currentScreen == null) toggle()
                }
                0 -> {
                    if(key.code != GLFW_KEY_UNKNOWN && event.key == key
                        && mode == Mode.HOLD) toggle()
                }
            }
        }

        private fun toggle() {
            if (enabled) {
                enabled = false
                onDisable()
            } else {
                onEnable()
                enabled = true
            }
        }

        enum class Mode { // TODO don't show or change in gui if key is UNKNOWN
            TOGGLE, HOLD
        }
    }

}