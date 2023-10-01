package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.gui.screen.Screen

class SetScreenEvent(val screen: Screen?) : Event()