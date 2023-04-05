package me.chell.blackout.impl.gui.items

import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.friends
import me.chell.blackout.api.util.isFriend
import me.chell.blackout.api.util.mc
import me.chell.blackout.impl.gui.GuiItem
import me.chell.blackout.impl.gui.buttons.RunnableButton
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW

class AddFriendItem(override var x: Int, override var y: Int): GuiItem() {

    override val width = 239// 300-50-1-5-5
    override val height = 28

    private var input = ""
    private var listening = false

    override val button = RunnableButton(this, Setting("Add", Runnable{}), false, "+")

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val center = y.toFloat() + (height / 2) - (mc.textRenderer.fontHeight / 2)
        mc.textRenderer.drawWithShadow(matrices, if(listening) input + "_" else "Add Friend:", x + margin.toFloat(), center, if(listening) -1 else 0xcacaca)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(listening) {
            if(!isFriend(input) && mouseX >= x + width - this.button.width - margin - margin && mouseX <= x + width && mouseY >= y && mouseY <= y + height) friends.add(input)
            input = ""
            listening = false
            return true
        } else if(mouseX >= x && mouseX <= x + width - this.button.width && mouseY >= y && mouseY <= y + height) {
            listening = true
            return true
        }
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when(keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> {
                if(!listening) return false
                listening = false
                input = ""
                return true
            }
            GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_ENTER -> {
                listening = false
                if(!isFriend(input)) friends.add(input)
                input = ""
                return true
            }
            GLFW.GLFW_KEY_BACKSPACE -> {
                if(input.isNotEmpty()) input = input.dropLast(1)
                return true
            }
        }

        return false
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if(listening) {
            input += chr
            return true
        }
        return false
    }

    override fun onClose() {
        listening = false
        input = ""
    }
}