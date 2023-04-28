package me.chell.blackout.impl.gui

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.modName
import me.chell.blackout.impl.features.client.HudEditorFeature
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import java.awt.Color

object HudEditor: Screen(Text.of("$modName HUD Editor")) {

    val widgets = mutableListOf<Widget>()

    private var lastSelected: Widget? = null
    private var selected: Widget? = null

    fun clientInit() {
        for(feature in FeatureManager.features) {
            if(feature::class == HudEditorFeature::class) continue
            if(feature.category == Category.Hud) widgets.add(feature as Widget)
        }
    }

    fun select(widget: Widget) {
        if(selected == widget) return
        lastSelected = selected
        selected = widget
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        drawHorizontalLine(matrices, 0, width, height / 2, Color.CYAN.rgb)
        drawVerticalLine(matrices, (width / 2) - 1, -1, height, Color.CYAN.rgb)

        for(w in widgets) {
            if(w.mainSetting.value) w.render(matrices, mouseX, mouseY, delta)
        }

        renderGuides(matrices)
    }

    private fun renderGuides(matrices: MatrixStack?) {
        if(lastSelected != null) {
            drawHorizontalLine(matrices, 0, width, lastSelected!!.y.value, Color.GRAY.rgb)
            drawHorizontalLine(matrices, 0, width, lastSelected!!.y.value + lastSelected!!.height - 1, Color.GRAY.rgb)
            drawVerticalLine(matrices, lastSelected!!.x.value, -1, height, Color.GRAY.rgb)
            drawVerticalLine(matrices, lastSelected!!.x.value + lastSelected!!.width - 1, -1, height, Color.GRAY.rgb)

            drawHorizontalLine(matrices, 0, width, lastSelected!!.y.value + (lastSelected!!.height / 2), Color.GRAY.rgb)
            drawVerticalLine(matrices, lastSelected!!.x.value + (lastSelected!!.width / 2) - 1, -1, height, Color.GRAY.rgb)
        }

        if(selected != null) {
            drawHorizontalLine(matrices, 0, width, selected!!.y.value, Color.MAGENTA.rgb)
            drawHorizontalLine(matrices, 0, width, selected!!.y.value + selected!!.height - 1, Color.MAGENTA.rgb)
            drawVerticalLine(matrices, selected!!.x.value, -1, height, Color.MAGENTA.rgb)
            drawVerticalLine(matrices, selected!!.x.value + selected!!.width - 1, -1, height, Color.MAGENTA.rgb)

            drawHorizontalLine(matrices, 0, width, selected!!.y.value + (selected!!.height / 2), Color.MAGENTA.rgb)
            drawVerticalLine(matrices, selected!!.x.value + (selected!!.width / 2) - 1, -1, height, Color.MAGENTA.rgb)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for(w in widgets) {
            if(w.mainSetting.value && w.mouseClicked(mouseX, mouseY, button)) return true
        }
        selected = null
        lastSelected = null
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for(w in widgets) {
            w.mouseReleased()
        }
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when(keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> {
                close()
                return true
            }
            GLFW.GLFW_KEY_UP -> {
                selected ?: return false
                if(selected!!.y.value > 0) selected!!.y.value--
                return true
            }
            GLFW.GLFW_KEY_DOWN -> {
                selected ?: return false
                if(selected!!.y.value + selected!!.height < height) selected!!.y.value++
                return true
            }
            GLFW.GLFW_KEY_LEFT -> {
                selected ?: return false
                if(selected!!.x.value > 0) selected!!.x.value--
                return true
            }
            GLFW.GLFW_KEY_RIGHT -> {
                selected ?: return false
                if(selected!!.x.value + selected!!.width < width) selected!!.x.value++
                return true
            }
        }
        return false
    }

    override fun close() {
        selected = null
        lastSelected = null
        for(w in widgets) {
            w.mouseReleased()
        }
        mc.setScreen(ClientGUI)
    }

    override fun shouldPause() = false
}