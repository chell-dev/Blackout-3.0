package me.chell.blackout.impl.features.client

import me.chell.blackout.Blackout
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import net.minecraft.client.util.InputUtil

class GuiFeature : Feature("GUI Bind", Category.Client) {

    override var description = "Keybind to open this GUI"

    companion object {
        lateinit var instance: GuiFeature
    }

    init {
        instance = this
    }

    override val mainSetting = Setting(
        "Open",
        Bind.Action(InputUtil.GLFW_KEY_BACKSLASH, InputUtil.Type.KEYSYM, action = { mc.setScreen(Blackout.clientGUI) })
    )

}