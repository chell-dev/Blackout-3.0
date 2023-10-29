package me.chell.blackout.impl.gui.old.items

import com.mojang.logging.LogUtils
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Color
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.textRenderer
import me.chell.blackout.impl.gui.old.Button
import me.chell.blackout.impl.gui.old.GuiItem
import me.chell.blackout.impl.gui.old.Tab
import me.chell.blackout.impl.gui.old.buttons.*
import net.minecraft.client.gui.DrawContext
import java.io.File

@Suppress("unchecked_cast")
class SettingItem(val setting: Setting<*>, override var x: Int, override var y: Int, tab: Tab): GuiItem(tab) {

    companion object {
        fun getOffset(setting: Setting<*>) = 10 * setting.level
    }

    val offset = getOffset(setting)

    override val width = 300 - Tab.size - 1 - margin - margin - offset
    override var height = 26

    override val button = when(setting.value) {
        is Boolean -> BooleanButton(this, setting as Setting<Boolean>, false)
        is Bind.Action -> ActionBindButton(this, setting as Setting<Bind.Action>, false)
        is Bind.Toggle -> ToggleBindButton(this, setting as Setting<Bind.Toggle>, false)
        is Number -> SliderButton(this, setting as Setting<Number>, false)
        is Enum<*> -> EnumButton(this, setting as Setting<Enum<*>>, false)
        is Runnable -> RunnableButton(this, setting as Setting<Runnable>, false)
        is File -> FileButton(this, setting as Setting<File>, false)
        is Color -> ColorButton(this, setting as Setting<Color>, true)
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

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float){
        super.render(context, mouseX, mouseY, delta)

        val center = y + (height /2) - (mc.textRenderer.fontHeight/2)
        context.drawTextWithShadow(textRenderer, setting.name, x + margin, center, -1)
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