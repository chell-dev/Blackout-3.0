package me.chell.blackout.impl.gui

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.util.featureManager
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class HudEditor: Screen(Text.of("Blackout HUD Editor")) {

    val widgets = mutableListOf<Widget>()

    init {
        for(feature in featureManager.features) {
            if(feature.category == Category.Hud) widgets.add(feature as Widget)
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        for(w in widgets) {
            if(w.mainSetting.value) w.render(matrices, mouseX, mouseY, delta)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for(w in widgets) {
            if(w.mainSetting.value && w.mouseClicked(mouseX, mouseY, button)) return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for(w in widgets) {
            w.mouseReleased()
        }
        return false
    }

    override fun close() {
        for(w in widgets) {
            w.mouseReleased()
        }
        super.close()
    }

    override fun shouldPause() = false
}