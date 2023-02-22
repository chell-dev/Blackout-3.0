package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.util.InputUtil

// actions: 0 = release, 1 = press, 2 = repeat
abstract class InputEvent(val key: InputUtil.Key, val action: Int, val modifiers: Int): Event() {

    class Keyboard(key: InputUtil.Key, action: Int, modifiers: Int) : InputEvent(key, action, modifiers)
    class Mouse(key: InputUtil.Key, action: Int, modifiers: Int) : InputEvent(key, action, modifiers)

}