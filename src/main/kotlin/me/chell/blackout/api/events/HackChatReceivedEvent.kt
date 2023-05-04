package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event

class HackChatReceivedEvent(val nick: String, val text: String): Event()