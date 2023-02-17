package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.util.InputUtil

class KeyPressedEvent(val key: InputUtil.Key, val action: Int, val modifiers: Int): Event()