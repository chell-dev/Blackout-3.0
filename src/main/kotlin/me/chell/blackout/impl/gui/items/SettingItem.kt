package me.chell.blackout.impl.gui.items

import com.mojang.logging.LogUtils
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.impl.gui.Button
import me.chell.blackout.impl.gui.GuiItem
import me.chell.blackout.impl.gui.buttons.*
import net.minecraft.client.util.math.MatrixStack
import java.io.File

@Suppress("unchecked_cast")
class SettingItem(val setting: Setting<*>, override var x: Int, override var y: Int): GuiItem() {

    companion object {
        const val offset = 10
    }

    override val width = 229
    override val height = 26

    override val button = when(setting.value) {
        is Boolean -> BooleanButton(this, setting as Setting<Boolean>, false)
        is Bind.Action -> ActionBindButton(this, setting as Setting<Bind.Action>, false)
        is Bind.Toggle -> ToggleBindButton(this, setting as Setting<Bind.Toggle>, false)
        is Number -> SliderButton(this, setting as Setting<Number>, false)
        is Enum<*> -> EnumButton(this, setting as Setting<Enum<*>>, false)
        is Runnable -> RunnableButton(this, setting as Setting<Runnable>, false)
        is File -> FileButton(this, setting as Setting<File>, false)
        else -> {
            LogUtils.getLogger().warn("Cannot create button for setting ${setting.name}")
            object : Button(this, false) {
                override val x = 0
                override val y = 0
                override var width = 0
                override val height = 0
            }
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float){
        super.render(matrices, mouseX, mouseY, delta)

        val center = y.toFloat() + (height /2) - (mc.textRenderer.fontHeight/2)
        mc.textRenderer.drawWithShadow(matrices, setting.name, x + margin.toFloat(), center, -1)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return this.button.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return this.button.mouseReleased(mouseX, mouseY, button)
    }

    override fun onClose() {
        button.onClose()
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return this.button.keyPressed(keyCode, scanCode, modifiers)
    }

}