package me.chell.blackout.api.util

import me.chell.blackout.impl.features.client.Messages
import me.chell.blackout.impl.gui.newgui.util.Rectangle
import me.chell.blackout.mixin.accessors.ChatHudAccessor
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.MessageIndicator
import net.minecraft.text.LiteralTextContent
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Vec2f
import java.util.function.UnaryOperator

fun StringBuilder.setString(text: String): StringBuilder = this.replace(0, this.length, text)

fun DrawContext.drawTrimmedTextWithShadow(text: String, x: Float, y: Float, maxWidth: Int, color: Int) {
    var y = y
    for(line in textRenderer.wrapLines(Text.of(text), maxWidth)) {
        drawTextWithShadow(textRenderer, line, x.toInt(), y.toInt(), color)
        y += textRenderer.fontHeight
    }
}

fun DrawContext.drawAlignedTextWithShadow(text: String, x: Float, y: Float, width: Float, height: Float, horizontal: HAlign, vertical: VAlign, color: Int) {
    val textWidth = textRenderer.getWidth(text)

    val textX = when(horizontal) {
        HAlign.Left -> x
        HAlign.Right -> x + width - textWidth
        HAlign.Center -> x + (width / 2f) - (textWidth / 2f)
    }

    val textY = when(vertical) {
        VAlign.Top, VAlign.Middle -> y
        VAlign.Bottom -> y + height - textRenderer.fontHeight
    }

    drawTextWithShadow(textRenderer, text, textX.toInt(), textY.toInt(), color)
}

fun DrawContext.drawText(text: String, shadow: Boolean, rectangle: Rectangle, horizontal: HAlign, vertical: VAlign, color: Int, scale: Float = 1f, padding: Vec2f = Vec2f(0f, 0f)) {
    val textWidth = textRenderer.getWidth(text) * scale
    val fontHeight = textRenderer.fontHeight * scale

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

    drawText(textRenderer, text, (textX / scale).toInt(), (textY / scale).toInt(), color, shadow)

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