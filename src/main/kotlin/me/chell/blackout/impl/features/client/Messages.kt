package me.chell.blackout.impl.features.client

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.BindEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.MutableText
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.modName
import me.chell.blackout.mixin.accessors.ChatHudAccessor
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object Messages: Feature("Messages", Category.Client) {

    override val mainSetting = Setting("Empty", null)

    val permanent = register(Setting("Permanent", true))

    private val keybinds = register(Setting("Keybinds", false))

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

}