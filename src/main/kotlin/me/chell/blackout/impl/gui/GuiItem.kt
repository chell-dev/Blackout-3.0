package me.chell.blackout.impl.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.util.modId
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

abstract class GuiItem: DrawableHelper() {

    companion object {
        const val margin = 5
    }

    abstract val width: Int
    abstract val height: Int
    abstract var x: Int
    abstract var y: Int

    abstract val button: Button

    private val background = Identifier(modId, "textures/gui/item.png")
    private val border = Identifier(modId, "textures/gui/item_border.png")

    open fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float){
        RenderSystem.setShaderTexture(0, background)
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.59f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        drawTexture(matrices, x, y, 0f, 0f, width, height, width, height)

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, border)
        drawTexture(matrices, x, y, 0f, 0f, width, height, width, height)

        button.render(matrices, mouseX, mouseY, delta)
    }

    open fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean = false

}