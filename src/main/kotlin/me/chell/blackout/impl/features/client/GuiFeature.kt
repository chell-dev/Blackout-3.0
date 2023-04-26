package me.chell.blackout.impl.features.client

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.impl.gui.ClientGUI
import net.minecraft.client.util.InputUtil

object GuiFeature: Feature("GUI Bind", Category.Client) {

    override var description = "Keybind to open this GUI"

    override val mainSetting = Setting("Open", Bind.Action(InputUtil.GLFW_KEY_BACKSLASH, InputUtil.Type.KEYSYM, action = { mc.setScreen(ClientGUI) }))

}