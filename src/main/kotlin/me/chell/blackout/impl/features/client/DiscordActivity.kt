package me.chell.blackout.impl.features.client

import dev.cbyrne.kdiscordipc.KDiscordIPC
import dev.cbyrne.kdiscordipc.data.activity.Activity
import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.ServerEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

object DiscordActivity: ToggleFeature("Discord RPC", Category.Client) {

    private var shouldUpdate = false

    private val client = KDiscordIPC("1077286468537569361")

    private var rpc = Activity(
        details = "Main Menu",
        assets = Activity.Assets(largeImage = "logo", smallImage = "logo"),
        buttons = mutableListOf(Activity.Button("Github", "https://github.com/chell-dev/Blackout-3.0")),
        timestamps = Activity.Timestamps(System.currentTimeMillis(), null)
    )

    init {
        run { client.connect() }
        EventManager.register(this)
    }

    override fun onEnable() {
        run { client.activityManager.setActivity(rpc) }
        shouldUpdate = true
    }

    override fun onDisable() {
        shouldUpdate = false
        run {  client.activityManager.clearActivity() }
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

        if(shouldUpdate) run { client.activityManager.setActivity(rpc) }
    }

    private fun run(function: suspend () -> Unit) = suspend { function.invoke() }.startCoroutine(object: Continuation<Unit> {
        override val context: CoroutineContext = EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) {
            result.onFailure { ex : Throwable -> throw ex }
        }
    })
}