package me.chell.blackout.impl.gui.newgui

import me.chell.blackout.impl.gui.newgui.tabs.FeaturesTab
import net.minecraft.client.util.math.MatrixStack

object Navbar {

    val tabs = listOf(FeaturesTab, Tab("Console", 1))
    var selected = tabs[0]

    fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        for(tab in tabs) {
            tab.renderTab(matrices, mouseX, mouseY, delta)
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for(tab in tabs) {
            if(tab.rect.contains(mouseX, mouseY)) {
                selected = tab
                return true
            }
        }
        return false
    }

}