package me.chell.blackout.impl.features.client

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.ServerEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.eventManager
import net.arikia.dev.drpc.DiscordEventHandlers
import net.arikia.dev.drpc.DiscordRPC
import net.arikia.dev.drpc.DiscordRichPresence

class DiscordActivity: ToggleFeature("Discord RPC", Category.Client, false) {

    private var shouldUpdate = false

    private val rpc = DiscordRichPresence.Builder("")
        .setDetails("Main Menu")
        .setBigImage("logo", "github.com/chell-dev/Blackout-3.0")
        .setSmallImage("logo", "")
        .setStartTimestamps(System.currentTimeMillis() / 1000)
        .build()

    init {
        DiscordRPC.discordInitialize("1077286468537569361", DiscordEventHandlers(), false)

        eventManager.register(this)
    }

    override fun onEnable() {
        DiscordRPC.discordUpdatePresence(rpc)
        shouldUpdate = true
    }

    override fun onDisable() {
        shouldUpdate = false
        DiscordRPC.discordClearPresence()
    }

    @EventHandler
    fun onMultiPlayer(event: ServerEvent.MultiPlayer) {
        rpc.details = "Playing Multiplayer"
        if(shouldUpdate) DiscordRPC.discordUpdatePresence(rpc)
    }

    @EventHandler
    fun onSinglePlayer(event: ServerEvent.SinglePlayer) {
        rpc.details = "Playing Singleplayer"
        if(shouldUpdate) DiscordRPC.discordUpdatePresence(rpc)
    }

    @EventHandler
    fun onLan(event: ServerEvent.Lan) {
        rpc.details = "Playing LAN"
        if(shouldUpdate) DiscordRPC.discordUpdatePresence(rpc)
    }

    @EventHandler
    fun onRealms(event: ServerEvent.Realms) {
        rpc.details = "Playing Realms"
        if(shouldUpdate) DiscordRPC.discordUpdatePresence(rpc)
    }

    @EventHandler
    fun onDisconnect(event: ServerEvent.Disconnect) {
        rpc.details = "Main Menu"
        if(shouldUpdate) DiscordRPC.discordUpdatePresence(rpc)
    }
}