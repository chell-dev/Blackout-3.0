package me.chell.blackout.impl.gui.old.buttons

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.modId
import me.chell.blackout.impl.gui.old.Button
import me.chell.blackout.impl.gui.old.GuiItem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier

class BooleanButton(private val parent: GuiItem, private val setting: Setting<Boolean>, expandable: Boolean): Button(parent, expandable) {

    companion object {
        val on = Identifier(modId, "textures/gui/button_on.png")
        val off = Identifier(modId, "textures/gui/button_off.png")
    }

    override var width = 32
    override val height = 16

    override val x get() = parent.x + parent.width - GuiItem.margin - width
    override val y get() = parent.y + (parent.height / 2) - (height / 2)

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val texture = if (setting.value) on else off

        RenderSystem.setShaderTexture(0, texture)

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        context.drawTexture(texture, x, (parent.y + (parent.height / 2) - (height / 2)), 0f, 0f, width, height, width, height)

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            setting.value = !setting.value
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

}