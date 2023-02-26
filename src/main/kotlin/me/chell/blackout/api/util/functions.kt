package me.chell.blackout.api.util

import net.minecraft.client.font.TextRenderer
import net.minecraft.text.StringVisitable

fun StringBuilder.setString(text: String): StringBuilder = this.replace(0, this.length, text)

fun TextRenderer.drawTrimmedWithShadow(text: String, x: Int, y: Int, maxWidth: Int, color: Int) {
    val text = StringVisitable.plain(text)
    //drawTrimmed(text, x + 1, y + 1, maxWidth, 0x3f3f3f)  // TODO HOW. KILL ME. FUCK MICROSOFT WHY DID THEY MAKE EVERYTHING SO STUPIDLY COMPLICATED
    drawTrimmed(text, x, y, maxWidth, color)
}