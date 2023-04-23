package me.chell.blackout.impl.gui.buttons

import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.impl.gui.Button
import me.chell.blackout.impl.gui.GuiItem
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import org.lwjgl.glfw.GLFW

class ActionBindButton(private val parent: GuiItem, setting: Setting<Bind.Action>, expandable: Boolean) :
    Button(parent, expandable) {

    override var width = 0
    override val height = mc.textRenderer.fontHeight

    override val x get() = parent.x + parent.width - GuiItem.margin - width
    override val y get() = parent.y + (parent.height / 2) - (height / 2)

    private val bind = setting.value

    private var listening = false

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        var text = if (listening) "[ . . . ]" else "[ ${bind.key.localizedText.string} ]"
        if (text.length == 5) text = text.uppercase()
        width = mc.textRenderer.getWidth(text)
        mc.textRenderer.drawWithShadow(matrices, text, x.toFloat(), y.toFloat(), -1) // 0xa100ff

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (listening) {
            bind.setKey(button, InputUtil.Type.MOUSE)
            listening = false
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            return true
        }

        if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            listening = true
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            return true
        }

        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (listening) {
            when (keyCode) {
                GLFW.GLFW_KEY_ESCAPE -> {
                    listening = false
                }

                GLFW.GLFW_KEY_DELETE -> {
                    bind.setKey(GLFW.GLFW_KEY_UNKNOWN, InputUtil.Type.KEYSYM)
                    listening = false
                    mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                }

                else -> {
                    bind.setKey(keyCode, InputUtil.Type.KEYSYM)
                    listening = false
                    mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                }
            }
            return true
        }

        return false
    }

    override fun onClose() {
        listening = false
    }
}