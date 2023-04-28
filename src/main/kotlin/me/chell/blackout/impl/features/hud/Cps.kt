package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Color
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.util.math.MatrixStack

object Cps: Widget("Crystal Per Second") {

    override var description = "How many crystals AutoCrystal placed in the last 20 ticks."

    private val color = register(Setting("Color", Color.white()))

    override var width = 50
    override var height = textRenderer.fontHeight

    var value = 0

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val text = "$value CPS"
        width = textRenderer.getWidth(text)
        textRenderer.drawWithShadow(matrices, text, x.value.toFloat(), y.value.toFloat(), color.value.rgb)
    }

}