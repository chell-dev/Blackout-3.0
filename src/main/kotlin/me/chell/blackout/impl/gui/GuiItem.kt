package me.chell.blackout.impl.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.util.modId
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import java.awt.Color

abstract class GuiItem(val tab: Tab) {

    companion object {
        const val margin = 5
    }

    abstract val width: Int
    abstract var height: Int
    abstract var x: Int
    abstract var y: Int

    abstract val button: Button

    private val background = Identifier(modId, "textures/gui/item.png")
    private val border = Identifier(modId, "textures/gui/item_border.png")

    open fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float){
        /*
        RenderSystem.setShaderTexture(0, background)
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.59f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        drawTexture(x, y, 0f, 0f, width, height, width, height)

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, border)
        drawTexture(x, y, 0f, 0f, width, height, width, height)
         */

        val border = 1
        val color = Color(161, 0, 255).rgb

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

        context.fill(x, y, x + width, y + border, color)
        context.fill(x + width - border, y + border, x + width, y + height, color)
        context.fill(x, y + height, x + width, y + height - border, color)
        context.fill(x, y + border, x + border, y + height, color)

        button.render(context, mouseX, mouseY, delta)
    }

    open fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean = false

    open  fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean = false

    open fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false

    open fun charTyped(chr: Char, modifiers: Int): Boolean = false

    open fun onClose() {}

}