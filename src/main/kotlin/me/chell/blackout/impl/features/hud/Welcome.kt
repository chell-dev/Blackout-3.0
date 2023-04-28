package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Color
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.util.math.MatrixStack

object Welcome: Widget("Welcome") {
    override var width = 10
    override var height = textRenderer.fontHeight

    private val format = register(Setting("Format", "Welcome, %s!"))
    private val color = register(Setting("Color", Color.white()))

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val text = String.format(format.value, mc.session.username)
        width = textRenderer.getWidth(text)
        textRenderer.drawWithShadow(matrices, text, x.value.toFloat(), y.value.toFloat(), color.value.rgb)
    }

}