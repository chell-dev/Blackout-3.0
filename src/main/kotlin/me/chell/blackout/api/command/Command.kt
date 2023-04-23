package me.chell.blackout.api.command

abstract class Command(vararg aliases: String, val description: String = "No description.") {
    val aliases = aliases.toList()

    abstract fun run(args: String)
}