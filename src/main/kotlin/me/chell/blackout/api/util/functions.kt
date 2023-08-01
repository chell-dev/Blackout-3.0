package me.chell.blackout.api.util

import me.chell.blackout.impl.features.client.Messages
import me.chell.blackout.impl.gui.newgui.util.Rectangle
import me.chell.blackout.mixin.accessors.ChatHudAccessor
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralTextContent
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Vec2f
import java.util.function.UnaryOperator

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

fun TextRenderer.draw(matrices: MatrixStack, text: String, shadow: Boolean, rectangle: Rectangle, horizontal: HAlign, vertical: VAlign, color: Int, scale: Float = 1f, padding: Vec2f = Vec2f(0f, 0f)) {
    val textWidth = getWidth(text) * scale
    val fontHeight = fontHeight * scale

    val textX = when(horizontal) {
        HAlign.Left -> rectangle.x + padding.x
        HAlign.Right -> rectangle.x + rectangle.width - textWidth - padding.x
        HAlign.Center -> rectangle.x + (rectangle.width / 2f) - (textWidth / 2f)
    }

    val textY = when(vertical) {
        VAlign.Top -> rectangle.y + padding.y
        VAlign.Bottom -> rectangle.y + rectangle.height - fontHeight - padding.y
        VAlign.Middle -> rectangle.y + (rectangle.height / 2f) - (fontHeight / 2f)
    }

    matrices.push()
    matrices.scale(scale, scale, 1f)

    if(shadow) drawWithShadow(matrices, text, textX / scale, textY / scale, color)
    else draw(matrices, text, textX / scale, textY / scale, color)

    matrices.scale(1f, 1f, 1f)
    matrices.pop()
}

operator fun Formatting.plus(string: String): String = toString() + string

operator fun Formatting.plus(other: Formatting): String = toString() + other.toString()

fun MutableText(string: String, styleUpdater: UnaryOperator<Style> = UnaryOperator{it}): MutableText = MutableText.of(LiteralTextContent(string)).styled(styleUpdater)

fun String.toText(): Text = Text.of(this)
fun String.toMutableText(): MutableText = Text.of(this) as MutableText

private val indicator = MessageIndicator(0xA100FF, null, Text.of(modName), modName)

fun sendClientMessage(message: Text) {
    val messages = (mc.inGameHud.chatHud as ChatHudAccessor).visibleMessages
    if(!Messages.permanent.value) messages.remove(messages.firstOrNull { it.indicator == indicator })
    mc.inGameHud.chatHud.addMessage(message, null, indicator)
}