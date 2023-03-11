package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.util.modName
import me.chell.blackout.api.util.modVersion
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.util.math.MatrixStack

class Watermark: Widget("Watermark") {

    override var width = 60
    override var height = textRenderer.fontHeight

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        textRenderer.drawWithShadow(matrices, "$modName $modVersion", x.value.toFloat(), y.value.toFloat(), -1)
    }
}