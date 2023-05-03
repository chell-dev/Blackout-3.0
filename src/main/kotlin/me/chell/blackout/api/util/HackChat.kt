package me.chell.blackout.api.util

import com.google.gson.Gson
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.HackChatReceivedEvent
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit

// https://github.com/hack-chat/main/blob/master/documentation/DOCUMENTATION.md
object HackChat {
    private val builder = HttpClient.newHttpClient().newWebSocketBuilder()
    private var ws: WebSocket? = null
    private val gson = Gson()
    private val nick = mc.session.username
    private const val channel = "blackout"

    fun join() {
        ws = builder.buildAsync(URI("wss://hack.chat/chat-ws"), Listener()).get()
        ws!!.sendText(gson.toJson(JoinCommand(channel, nick)), true)
    }

    fun chat(text: String) {
        ws?.sendText(gson.toJson(ChatCommand(text)), true)
    }

    fun disconnect() {
        ws?.sendText(gson.toJson(DisconnectCommand()), true)
        ws?.sendClose(1000, "Disconnected")?.get(3, TimeUnit.SECONDS)
        ws = null
    }

    private class Listener: WebSocket.Listener {
        override fun onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletionStage<*>? {
            //println(data)
            if(data.startsWith("{\"cmd\":\"chat\"")) {
                val message = Gson().fromJson(data.toString(), ReceivedChat::class.java)
                EventManager.post(HackChatReceivedEvent(message.nick, message.text.replace("\n", " ")))
            }
            return super.onText(webSocket, data, last)
        }

        override fun onClose(webSocket: WebSocket, statusCode: Int, reason: String): CompletionStage<*>? {
            //println("$statusCode - $reason")
            return super.onClose(webSocket, statusCode, reason)
        }

        override fun onError(webSocket: WebSocket?, error: Throwable) {
            error.printStackTrace()
            super.onError(webSocket, error)
        }
    }

    private class JoinCommand(val channel: String, val nick: String) {val cmd = "join"}
    private class ChatCommand(val text: String) {val cmd = "chat"}
    private class DisconnectCommand {val cmd = "disconnect"}
    private class ReceivedChat(val nick: String, val text: String, val time: Long)
}