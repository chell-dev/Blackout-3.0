package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event

data class HackChatReceivedEvent(val nick: String, val text: String): Event()