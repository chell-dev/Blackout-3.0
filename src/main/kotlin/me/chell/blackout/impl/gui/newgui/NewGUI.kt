package me.chell.blackout.impl.gui.newgui

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.util.Color
import me.chell.blackout.api.util.modId
import me.chell.blackout.impl.gui.newgui.util.Border
import me.chell.blackout.impl.gui.newgui.util.Rectangle
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object NewGUI: Screen(Text.of(":p")) {

    val window = Rectangle(0f, 50f, 770f, 350f, Color(0x60151515), Border(Border.Position.Inside, Border.Line(1f, Color.sync()), Border.Line(1f, Color.sync()), Border.Line(1f, Color.sync()), Border.Line(1f, Color.sync())))

    val tab get() = Navbar.selected

    override fun onDisplayed() {
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)

        window.x = (width / 2f) - (window.width/2f)

        window.draw(matrices)

        val bannerTexture = Identifier(modId, "textures/gui/banner.png")

        RenderSystem.setShaderTexture(0, bannerTexture)
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.25f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        drawTexture(matrices, (window.x + window.width - 310).toInt(), (window.y + window.height - 75).toInt(), 0f, 0f, 300, 75, 300, 75)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

        tab.render(matrices, mouseX, mouseY, delta)

        Navbar.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super.mouseMoved(mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(Navbar.mouseClicked(mouseX, mouseY, button)) return true

        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun shouldPause() = false
}