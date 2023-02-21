package me.chell.blackout.impl.features.client

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.KeyPressedEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.modName
import me.chell.blackout.api.util.player
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.impl.gui.ClientGUI
import net.minecraft.client.util.InputUtil

class GuiFeature: Feature("Open $modName GUI", Category.Client) {
    override val mainSetting = Setting("Open", InputUtil.fromKeyCode(InputUtil.GLFW_KEY_BACKSLASH, -1))

    init {
        eventManager.register(this)
    }

    @EventHandler
    fun onKeyPressed(event: KeyPressedEvent) {
        if (event.key == mainSetting.value && event.action == 1 && mc.currentScreen == null && mc.player != null && !player.isDead) {
            mc.setScreen(ClientGUI())
        }
    }

}