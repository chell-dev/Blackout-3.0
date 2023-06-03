package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event

data class PlayerKnockbackEvent(var canceled: Boolean = false): Event()