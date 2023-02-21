package me.chell.blackout.impl.gui.buttons

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.modId
import me.chell.blackout.api.value.Setting
import me.chell.blackout.impl.gui.Button
import me.chell.blackout.impl.gui.GuiItem
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier

class BooleanButton(parent: GuiItem, private val setting: Setting<Boolean>, expandable: Boolean): Button(parent, expandable) {

    private val on = Identifier(modId, "textures/gui/button_on.png")
    private val off = Identifier(modId, "textures/gui/button_off.png")

    override val width = 32
    override val height = 16

    override val x = parent.x + parent.width - GuiItem.margin - width
    override val y = parent.y + (parent.height / 2) - (height / 2)

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if (setting.value) RenderSystem.setShaderTexture(0, on)
        else RenderSystem.setShaderTexture(0, off)

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        DrawableHelper.drawTexture(matrices, x, y, 0f, 0f, width, height, width, height)

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + width) {
            setting.value = !setting.value
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

}