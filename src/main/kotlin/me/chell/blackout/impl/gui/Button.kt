package me.chell.blackout.impl.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.util.modId
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

abstract class Button(private val parent: GuiItem, private val expandable: Boolean) {

    abstract val x: Int
    abstract val y: Int
    abstract val width: Int
    abstract val height: Int

    private val icon = Identifier(modId, "textures/gui/settings.png")
    private val iconSize = GuiItem.height / 2
    private val iconX get() = x - iconSize - GuiItem.margin
    private val iconY = parent.y + (GuiItem.height / 2) - (iconSize / 2)

    open fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float){
        if(expandable) {
            RenderSystem.setShaderTexture(0, icon)
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.enableDepthTest()
            DrawableHelper.drawTexture(matrices, iconX, iconY, 0f, 0f, iconSize, iconSize, iconSize, iconSize)
        }
    }

    open fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button == 1 && mouseX >= parent.x && mouseX <= parent.x + parent.width && mouseY >= parent.y && mouseY <= parent.y + GuiItem.height) {
            return true
        }

        return false
    }

}