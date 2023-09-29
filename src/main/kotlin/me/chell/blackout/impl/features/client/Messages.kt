package me.chell.blackout.impl.features.client

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.BindEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import me.chell.blackout.mixin.accessors.ChatHudAccessor
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object Messages: Feature("Messages", Category.Client) {

    override val mainSetting = Setting("Empty", null)

    val permanent = register(Setting("Permanent", true))

    private val keybinds = register(Setting("Keybinds", false))
    private val deathCoords = register(Setting("Death Location", false))
    private val kd = register(Setting("K/D", false))

    /*
    private val deathLocation = register(Setting("Death Location", null))
    private val deathSelf = register(Setting("D Self", false, level = 2))
    private val deathFriends = register(Setting("D Friends", false, level = 2))
    private val deathOthers = register(Setting("D Others", false, level = 2))

    private val strength = register(Setting("Strength Potion", null))
    private val strengthFriends = register(Setting("St Friends", false, level = 2))
    private val strengthOthers = register(Setting("St Others", false, level = 2))

    private val speed = register(Setting("Speed Potion", null))
    private val speedFriends = register(Setting("Sp Friends", false, level = 2))
    private val speedOthers = register(Setting("Sp Others", false, level = 2))

    private val rd = register(Setting("Render Distance", null))
    private val rdFriends = register(Setting("Rd Friends", false, level = 2))
    private val rdOthers = register(Setting("Rd Others", false, level = 2))

    private val totem = register(Setting("Totem pop", null))
    private val totemFriends = register(Setting("Rd Friends", false, level = 2))
    private val totemOthers = register(Setting("Rd Others", false, level = 2))
    */

    init {
        EventManager.register(this)
    }

    private val kdIndicator = MessageIndicator(0xA100FF, null, Text.of("Kills/Deaths"), "Kills/Deaths")

    fun onKill(target: PlayerEntity) {
        if(kd.value && mc.currentServerEntry != null) sendKdMessage("${Formatting.RED}You killed ${target.name}!".toText())
    }

    fun onDeath() {
        if(kd.value && mc.currentServerEntry != null) sendKdMessage("${Formatting.RED}You died!".toText())
        if(deathCoords.value) sendClientMessage("${Formatting.GRAY}You died at x${player.blockX} y${player.blockY} z${player.blockZ}".toText())
    }

    @EventHandler
    fun onBind(event: BindEvent) {
        if(!keybinds.value || mc.player == null) return
        sendKeybindMessage(event.bind)
    }

    private fun sendKeybindMessage(bind: Bind) {
        val text = MutableText(bind.name) { it.withColor(Formatting.GRAY) }

        if(bind is Bind.Toggle) {
            if(bind.enabled) {
                text.append(MutableText(" disabled.") { it.withColor(Formatting.RED) })
            } else {
                text.append(MutableText(" enabled.") { it.withColor(Formatting.GREEN) })
            }
        } else {
            text.append(MutableText(" activated.") { it.withColor(Formatting.AQUA) })
        }

        if(!permanent.value) (mc.inGameHud.chatHud as ChatHudAccessor).visibleMessages.removeAll { it.indicator?.loggedName == bind.name }
        mc.inGameHud.chatHud.addMessage(text, null, MessageIndicator(0xA100FF, null, Text.of(modName), bind.name))
    }

    private fun sendKdMessage(message: Text) {
        val kd = CombatTracker.servers.getOrDefault(mc.currentServerEntry!!.address, intArrayOf(0, 0))

        val messages = (mc.inGameHud.chatHud as ChatHudAccessor).visibleMessages
        if(!permanent.value) messages.remove(messages.firstOrNull { it.indicator == kdIndicator })

        mc.inGameHud.chatHud.addMessage(message.copy()
            .append(MutableText("Your K/D is now ${if(kd[0] == 0) "0.00" else (kd[0] / kd[1]).toString()}"){ it
                .withColor(Formatting.GRAY)
                .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("${kd[0]} Kills, ${kd[1]} Deaths")))
            }), null, kdIndicator)
    }

}