package me.chell.blackout.impl.gui.old.buttons

import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.textRenderer
import me.chell.blackout.impl.gui.old.Button
import me.chell.blackout.impl.gui.old.GuiItem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File

class FileButton(private val parent: GuiItem, private val setting: Setting<File>, expandable: Boolean): Button(parent, expandable) {

    override var width = 32
    override val height = 9

    override val x get() = parent.x + parent.width - GuiItem.margin - width
    override val y get() = parent.y + (parent.height / 2) - (height / 2)

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val text = setting.value.name
        width = textRenderer.getWidth(text)
        context.drawTextWithShadow(textRenderer, text, x, y, 0xcacaca)

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            val select = TinyFileDialogs.tinyfd_openFileDialog("Select File", setting.value.absolutePath, null, null, false)
            if(select != null) {
                setting.value = File(select)
                mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            }
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

}
