package me.chell.blackout.impl.gui.buttons

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.modId
import me.chell.blackout.api.util.textRenderer
import me.chell.blackout.impl.gui.Button
import me.chell.blackout.impl.gui.GuiItem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SliderButton(private val parent: GuiItem, private val setting: Setting<Number>, expandable: Boolean): Button(parent, expandable) {

    override val x: Int get() =  parent.x + parent.width - GuiItem.margin - width
    override val y: Int get() = parent.y + (parent.height / 2) - (height / 2)

    private val min = setting.min!!.toFloat()
    private val max = setting.max!!.toFloat()

    override var width = 120
    private var sliderWith = (width * min(max((setting.value.toFloat() - min) / max, 0f), 1f)).toInt()
    override val height = 18
    private var dragging = false

    private val textOffset = (parent.height / 2f) - (textRenderer.fontHeight / 2f)

    private val frame = Identifier(modId, "textures/gui/slider_frame.png")
    private val fill = Identifier(modId, "textures/gui/slider_fill.png")

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if(dragging) {
            val normalized = min(max((mouseX - x) / width.toFloat(), 0f), 1f)
            val value = min + ((max - min) * normalized)
            when(setting.value) {
                is Int -> setting.value = value.roundToInt()
                is Double -> setting.value = (value * 10.0).roundToInt() / 10.0
                is Float -> setting.value = (value * 10.0f).roundToInt() / 10.0f
            }
            sliderWith = (width * normalized).toInt()
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()

        RenderSystem.setShaderTexture(0, fill)
        drawTexture(matrices, x, y, 0f, 0f, sliderWith, height, width, height)
        RenderSystem.setShaderTexture(0, frame)
        drawTexture(matrices, x, y, 0f, 0f, width, height, width, height)

        val text = setting.display.invoke(setting.value)
        textRenderer.drawWithShadow(matrices, text, x + width - textRenderer.getWidth(text).toFloat() - GuiItem.margin, parent.y + textOffset + 1, -1)

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if(button == 0) {
                dragging = true
            }
        }

        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        dragging = false
        return false
    }
}