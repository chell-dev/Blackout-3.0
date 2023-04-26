package me.chell.blackout.impl.features.client

import me.chell.blackout.api.feature.ActionBindFeature
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.util.mc
import me.chell.blackout.impl.gui.ClientGUI
import org.lwjgl.glfw.GLFW

object GuiFeature: ActionBindFeature("GUI Bind", Category.Client, GLFW.GLFW_KEY_BACKSLASH) {

    override var description = "Keybind to open this GUI"

    override fun activate() = mc.setScreen(ClientGUI)

}