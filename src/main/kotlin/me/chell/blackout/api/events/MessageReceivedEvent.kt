package me.chell.blackout.api.events

import com.mojang.authlib.GameProfile
import me.chell.blackout.api.event.Event
import net.minecraft.network.message.MessageType
import net.minecraft.network.message.SignedMessage
import net.minecraft.text.Text

abstract class MessageReceivedEvent(val phase: Phase): Event() {
    class Chat(var message: SignedMessage, var sender: GameProfile, var params: MessageType.Parameters, phase: Phase): MessageReceivedEvent(phase)
    class Profileless(var content: Text, var params: MessageType.Parameters, phase: Phase): MessageReceivedEvent(phase)
    class Game(var message: Text, var overlay: Boolean, phase: Phase): MessageReceivedEvent(phase)

    enum class Phase {
        Pre, Post
    }
}