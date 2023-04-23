package me.chell.blackout.impl.gui.buttons

import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.textRenderer
import me.chell.blackout.impl.gui.Button
import me.chell.blackout.impl.gui.GuiItem
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File

class FileButton(private val parent: GuiItem, private val setting: Setting<File>, expandable: Boolean) :
    Button(parent, expandable) {

    override var width = 32
    override val height = 9

    override val x get() = parent.x + parent.width - GuiItem.margin - width
    override val y get() = parent.y + (parent.height / 2) - (height / 2)

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val text = setting.value.name
        width = textRenderer.getWidth(text)
        textRenderer.drawWithShadow(matrices, text, x.toFloat(), y.toFloat(), 0xcacaca)

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            val select =
                TinyFileDialogs.tinyfd_openFileDialog("Select File", setting.value.absolutePath, null, null, false)
            if (select != null) {
                setting.value = File(select)
                mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            }
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

}
