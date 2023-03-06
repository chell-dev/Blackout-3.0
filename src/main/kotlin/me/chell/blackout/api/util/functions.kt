package me.chell.blackout.api.util

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

fun StringBuilder.setString(text: String): StringBuilder = this.replace(0, this.length, text)

fun TextRenderer.drawTrimmedWithShadow(matrices: MatrixStack?, text: String, x: Float, y: Float, maxWidth: Int, color: Int) {
    var y = y
    for(line in wrapLines(Text.of(text), maxWidth)) {
        drawWithShadow(matrices, line, x, y, color)
        y += fontHeight
    }
}