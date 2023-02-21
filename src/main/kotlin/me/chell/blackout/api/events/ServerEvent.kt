package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event

abstract class ServerEvent: Event() {
    class MultiPlayer: ServerEvent()
    class Lan: ServerEvent()
    class Realms: ServerEvent()
    class SinglePlayer: ServerEvent()

    class Disconnect: ServerEvent()
}