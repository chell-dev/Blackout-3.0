package me.chell.blackout.api.addon

import me.chell.blackout.api.command.Command
import me.chell.blackout.api.feature.Feature

abstract class Addon {
    open val features: List<Feature> = emptyList()
    open val commands: List<Command> = emptyList()

    open fun init() {}

    open fun shutdown() {}
}