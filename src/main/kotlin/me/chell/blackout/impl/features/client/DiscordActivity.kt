package me.chell.blackout.impl.features.client

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.ServerEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import net.arikia.dev.drpc.DiscordEventHandlers
import net.arikia.dev.drpc.DiscordRPC
import net.arikia.dev.drpc.DiscordRichPresence

object DiscordActivity: ToggleFeature("Discord RPC", Category.Client) {

    private var shouldUpdate = false

    private val rpc = DiscordRichPresence.Builder("")
        .setDetails("Main Menu")
        .setBigImage("logo", "github.com/chell-dev/Blackout-3.0")
        .setSmallImage("logo", "")
        .setStartTimestamps(System.currentTimeMillis() / 1000)
        .build()

    init {
        DiscordRPC.discordInitialize("1077286468537569361", DiscordEventHandlers(), false)

        EventManager.register(this)
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
    fun onMultiPlayer(event: ServerEvent) {
        rpc.details = when(event) {
            is ServerEvent.MultiPlayer -> "Playing Multiplayer"
            is ServerEvent.SinglePlayer -> "Playing Singleplayer"
            is ServerEvent.Lan -> "Playing LAN"
            is ServerEvent.Realms -> "Playing Realms"
            else -> "Main Menu"
        }

        if(shouldUpdate) DiscordRPC.discordUpdatePresence(rpc)
    }
}