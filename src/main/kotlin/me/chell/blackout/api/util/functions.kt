package me.chell.blackout.api.util

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting

fun StringBuilder.setString(text: String): StringBuilder = this.replace(0, this.length, text)

fun TextRenderer.drawTrimmedWithShadow(matrices: MatrixStack?, text: String, x: Float, y: Float, maxWidth: Int, color: Int) {
    var y = y
    for(line in wrapLines(Text.of(text), maxWidth)) {
        drawWithShadow(matrices, line, x, y, color)
        y += fontHeight
    }
}

fun TextRenderer.drawWithShadow(matrices: MatrixStack?, text: String, x: Float, y: Float, width: Float, height: Float, horizontal: HAlign, vertical: VAlign, color: Int) {
    val textWidth = getWidth(text)

    val textX = when(horizontal) {
        HAlign.Left -> x
        HAlign.Right -> x + width - textWidth
        HAlign.Center -> x + (width / 2) - (textWidth / 2f)
    }

    val textY = when(vertical) {
        VAlign.Top, VAlign.Middle -> y
        VAlign.Bottom -> y + height - fontHeight
    }

    drawWithShadow(matrices, text, textX, textY, color)
}

operator fun Formatting.plus(string: String): String = toString() + string

operator fun Formatting.plus(other: Formatting): String = toString() + other.toString()