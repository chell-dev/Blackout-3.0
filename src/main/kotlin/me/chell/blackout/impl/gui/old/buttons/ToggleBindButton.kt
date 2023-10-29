package me.chell.blackout.impl.gui.old.buttons

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.textRenderer
import me.chell.blackout.impl.gui.old.Button
import me.chell.blackout.impl.gui.old.GuiItem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.InputUtil
import net.minecraft.sound.SoundEvents
import org.lwjgl.glfw.GLFW

class ToggleBindButton(private val parent: GuiItem, setting: Setting<Bind.Toggle>, expandable: Boolean): Button(parent, expandable) {

    override var width = 0
    override val height = 16

    override val x get() = parent.x + parent.width - GuiItem.margin - width
    override val y get() = parent.y + (parent.height / 2) - (height / 2)

    private val bind = setting.value

    private var listening = false

    private var bindWidth = 0
    private var bindHeight = mc.textRenderer.fontHeight
    private val bindY get() = parent.y + (parent.height / 2) - (bindHeight / 2)
    private var buttonWidth = 32

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        var text = if(listening) " [ . . . ]" else " [ ${bind.key.localizedText.string} ]"
        if(text.length == 6) text = text.uppercase()
        val mode = if(bind.key.code == GLFW.GLFW_KEY_UNKNOWN) "" else bind.mode.name

        bindWidth = mc.textRenderer.getWidth(mode + text)
        context.drawTextWithShadow(textRenderer, mode + text, x, bindY, -1) // 0xa100ff

        width = bindWidth + buttonWidth + GuiItem.margin

        val texture = if (bind.enabled) BooleanButton.on else BooleanButton.off

        RenderSystem.setShaderTexture(0, texture)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        context.drawTexture(texture, x + bindWidth + GuiItem.margin, y, 0f, 0f, buttonWidth, height, buttonWidth, height)

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(listening) {
            bind.setKey(button, InputUtil.Type.MOUSE)
            listening = false
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            return true
        }

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if(mouseX >= x + bindWidth + GuiItem.margin) {
                if(button == 0) {
                    bind.enabled = !bind.enabled
                    mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                    return true
                }
            } else if(mouseY >= bindY && mouseY <= bindY + bindHeight) {
                if(button == 0) {
                    listening = true
                    mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                    return true
                }
                if(button == 2 && bind.key.code != GLFW.GLFW_KEY_UNKNOWN) {
                    if(bind.mode == Bind.Toggle.Mode.Toggle) bind.mode = Bind.Toggle.Mode.Hold
                    else bind.mode = Bind.Toggle.Mode.Toggle
                    mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                    return true
                }
            }
        }

        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if(listening) {
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