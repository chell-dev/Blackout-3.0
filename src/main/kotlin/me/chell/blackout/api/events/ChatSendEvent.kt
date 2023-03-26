package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event

class ChatSendEvent(var text: String, val isCommand: Boolean, var canceled: Boolean): Event()