package me.chell.blackout.api.command

import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.plus
import me.chell.blackout.impl.gui.Console
import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.network.ServerAddress
import net.minecraft.client.network.ServerInfo
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import org.lwjgl.glfw.GLFW
import kotlin.system.exitProcess

object CommandManager {

    val commands = mutableListOf<Command>()

    init {
        commands.add(object: Command("help", "h", "commands", description = "List all commands") {
            override fun run(args: String) {
                for(c in commands) {
                    val sb = StringBuilder()
                    //if(commands.indexOf(c) % 2 != 0) sb.append(Formatting.DARK_GRAY)
                    for(a in c.aliases) sb.append("$a, ")
                    Console.print("${sb.dropLast(2)} - ${c.description}")
                }
            }})

        commands.add(object: Command("clear", description = "Clear the console.") {
            override fun run(args: String) {
                Console.lines.clear()
            }})

        commands.add(object: Command("quit", "q", description = "Close Minecraft.") {
            override fun run(args: String) {
                exitProcess(0)
            }})

        commands.add(object: Command("disconnect", "dc", description = "Disconnect from the server.") {
            override fun run(args: String) {
                mc.networkHandler?.connection?.disconnect(Text.of("Disconnected."))
            }})

        commands.add(object: Command("connect", "play", description = "Connect to a server.") {
            override fun run(args: String) {
                if(args.trim().isEmpty()) {
                    Console.print(Formatting.RED + "connect <ip>")
                    return
                }
                mc.networkHandler?.connection?.disconnect(Text.of("Connecting to another server."))
                ConnectScreen.connect(MultiplayerScreen(TitleScreen()), mc, ServerAddress.parse(args), ServerInfo("Server", args, false))
            }})

        commands.add(object: Command("yaw", "setyaw", description = "Set your yaw.") {
            override fun run(args: String) {
                try {
                    mc.player?.yaw = args.toFloat()
                } catch (e: NumberFormatException) {
                    Console.print(Formatting.RED + "Invalid number!")
                }
            }})

        commands.add(object: Command("pitch", "setpitch", description = "Set your pitch.") {
            override fun run(args: String) {
                try {
                    mc.player?.pitch = args.toFloat()
                } catch (e: NumberFormatException) {
                    Console.print(Formatting.RED + "Invalid number!")
                }
            }})

        commands.add(object: Command("pos", "getpos", description = "Print your coordinates.") {
            override fun run(args: String) {
                mc.player ?: return
                Console.print("Vector: x${player.pos.x} y${player.pos.y} z${player.pos.z}")
                Console.print("Block: x${player.blockPos.x} y${player.blockPos.y} z${player.blockPos.z}")
            }})

        commands.add(object: Command("copypos", description = "Copy block pos to clipboard.") {
            override fun run(args: String) {
                mc.player ?: return
                GLFW.glfwSetClipboardString(mc.window.handle, "x${player.blockPos.x} y${player.blockPos.y} z${player.blockPos.z}")
                Console.print("Copied!")
            }})

        commands.add(object: Command("openfolder", "folder", description = "Open .minecraft/") {
            override fun run(args: String) {
                Util.getOperatingSystem().open(mc.runDirectory)
            }})

        commands.add(object: Command("shrug", description = "¯\\_(ツ)_/¯") {
            override fun run(args: String) {
                mc.networkHandler?.sendChatMessage("¯\\_(ツ)_/¯")
            }})
    }

    fun onCommand(input: String) {
        val hasSpace = input.contains(" ")
        val command = if(hasSpace) input.split(" ")[0] else input

        for(c in commands) {
            if(c.aliases.contains(command)) {
                c.run(input.substring(command.length + if(hasSpace) 1 else 0))
                return
            }
        }

        Console.print(Formatting.RED + "Unknown command!")

    }

}