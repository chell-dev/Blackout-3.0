package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event

data class RenderNametagEvent(var canceled: Boolean): Event()