package me.chell.blackout.impl.gui.buttons

import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.textRenderer
import me.chell.blackout.impl.gui.Button
import me.chell.blackout.impl.gui.GuiItem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents

class RunnableButton(private val parent: GuiItem, private val setting: Setting<Runnable>, expandable: Boolean, private val buttonText: String = "->"): Button(parent, expandable) {

    override var width = textRenderer.getWidth(buttonText)
    override val height = textRenderer.fontHeight

    override val x get() = parent.x + parent.width - GuiItem.margin - width
    override val y get() = parent.y + (parent.height / 2) - (height / 2)

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        context.drawTextWithShadow(textRenderer, buttonText, x, y, -1)

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button == 0 && mouseX >= parent.x && mouseX <= parent.x + parent.width && mouseY >= parent.y && mouseY <= parent.y + parent.height) {
            setting.value.run()
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

}