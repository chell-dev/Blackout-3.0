package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event

class PlayerKnockbackEvent(var canceled: Boolean = false): Event()