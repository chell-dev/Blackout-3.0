package me.chell.blackout.api.command

import me.chell.blackout.api.util.*
import me.chell.blackout.impl.commands.NameHistoryCommand
import me.chell.blackout.impl.gui.Console
import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.network.ServerAddress
import net.minecraft.client.network.ServerInfo
import net.minecraft.text.Text
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
                ConnectScreen.connect(MultiplayerScreen(TitleScreen()), mc, ServerAddress.parse(args), ServerInfo("Server", args, ServerInfo.ServerType.OTHER), false)
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

        commands.add(object: Command("chat", description = "hackchat") {
            override fun run(args: String) {
                if(args.length !in 1 until 150) Console.print("chat <message>")
                else HackChat.chat(args)
            }})

        commands.add(object: Command("kd", description = "Get your KD on this server.") {
            override fun run(args: String) {
                if(mc.currentServerEntry == null) {
                    Console.print(Formatting.RED + "You are not in a server!")
                } else {
                    val kd = CombatTracker.servers.getOrDefault(mc.currentServerEntry!!.address, intArrayOf(0, 0))
                    val k = kd[0]
                    val d = kd[1]
                    Console.print(if(k == 0) "0.00" else (k / d).toString())
                }
            }})

        commands.add(NameHistoryCommand)
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