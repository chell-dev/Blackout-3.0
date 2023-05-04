package me.chell.blackout.impl.features.client

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.ChatSendEvent
import me.chell.blackout.api.events.HackChatReceivedEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.HackChat
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import me.chell.blackout.impl.gui.Console
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object ChatFeature: ToggleFeature("Chat", Category.Client) {

    override var description = "Chat with other blackout users using the console or mc chat."

    private val prefix = register(Setting("Prefix", "@", description = "Prefix to send messages from minecraft chat."))

    override fun onEnable() {
        HackChat.join()
        super.onEnable()
    }

    override fun onDisable() {
        super.onDisable()
        HackChat.disconnect()
    }

    @EventHandler
    fun onChat(event: ChatSendEvent) {
        if(event.text.startsWith(prefix.value)) {
            event.canceled = true
            val sub = event.text.substring(prefix.value.length)
            if(sub.length < 150)
                HackChat.chat(sub)
            else
                player.sendMessage(Text.of("${Formatting.DARK_PURPLE}</blackout> ${Formatting.GRAY}Message can't be more than 150 characters long!"))
        }
    }

    @EventHandler
    fun onReceived(event: HackChatReceivedEvent) {
        val text = if(event.text.length < 150) event.text else "${Formatting.DARK_RED}[Long message]"
        mc.player?.sendMessage(Text.of("${Formatting.DARK_PURPLE}</blackout> ${Formatting.LIGHT_PURPLE}${event.nick}: ${Formatting.GRAY}$text"))
        Console.print("${Formatting.LIGHT_PURPLE}${event.nick}: ${Formatting.GRAY}$text")
    }

}