package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.util.math.MatrixStack

class Cps: Widget("CPS") {

    override var description = "(debug) Crystals per second"

    override var width = 50
    override var height = textRenderer.fontHeight

    var value = 0

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val text = "$value CPS"
        width = textRenderer.getWidth(text)
        textRenderer.drawWithShadow(matrices, text, x.value.toFloat(), y.value.toFloat(), -1)
    }

}