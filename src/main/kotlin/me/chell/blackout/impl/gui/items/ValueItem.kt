package me.chell.blackout.impl.gui.items

import com.mojang.logging.LogUtils
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.value.Value
import me.chell.blackout.impl.gui.Button
import me.chell.blackout.impl.gui.GuiItem
import me.chell.blackout.impl.gui.buttons.BooleanButton
import net.minecraft.client.util.math.MatrixStack

@Suppress("unchecked_cast")
class ValueItem(val value: Value<*>, override var x: Int, override var y: Int): GuiItem() {

    companion object {
        const val offset = 10
    }

    override val width = 229
    override val height = 26

    override val button = when(value.value) {
        is Boolean -> BooleanButton(this, value as Value<Boolean>, false)
        else -> {
            LogUtils.getLogger().warn("Cannot create button for setting ${value.name}")
            object : Button(this, false) {
                override val x = 0
                override val y = 0
                override val width = 0
                override val height = 0
            }
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float){
        super.render(matrices, mouseX, mouseY, delta)

        val center = y.toFloat() + (height /2) - (mc.textRenderer.fontHeight/2)
        mc.textRenderer.drawWithShadow(matrices, value.name, x + margin.toFloat(), center, -1)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return false
    }

}