package me.chell.blackout.impl.gui.items

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.modId
import me.chell.blackout.api.value.Value
import me.chell.blackout.impl.gui.Button
import me.chell.blackout.impl.gui.GuiItem
import me.chell.blackout.impl.gui.buttons.BooleanButton
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

@Suppress("unchecked_cast")
class FeatureItem(val feature: Feature, override var x: Int, override var y: Int): GuiItem() {

    override val width = 239// 300-50-1-5-5

    private var button: Button = when(feature.mainValue.value) {
        is Boolean -> BooleanButton(this, feature.mainValue as Value<Boolean>, feature.values.isNotEmpty())
        else -> object: Button(this, false){
            override val x = 0
            override val y = 0
            override val width = 0
            override val height = 0
        }
    }

    private val background = Identifier(modId, "textures/gui/item.png")
    private val border = Identifier(modId, "textures/gui/item_border.png")

    fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float){
        RenderSystem.setShaderTexture(0, background)
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.59f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        drawTexture(matrices, x, y, 0f, 0f, width, height, width, height)

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, border)
        drawTexture(matrices, x, y, 0f, 0f, width, height, width, height)

        val center = y.toFloat() + (height /2) - (mc.textRenderer.fontHeight/2)
        mc.textRenderer.drawWithShadow(matrices, feature.name, x + margin.toFloat(), center, -1)

        button.render(matrices, mouseX, mouseY, delta)
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return this.button.mouseClicked(mouseX, mouseY, button)
    }

}