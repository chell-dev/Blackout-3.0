package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Formatting

class Players: Widget("Players") {

    override var width = 10
    override var height = 10

    private val range = register(Setting("Range", 0, 0, 100, display = { if(it == 0) "Infinite" else it.toString() }))
    private val friendColor = register(Setting("Friend Color", Color.sync()))
    private val neutralColor = register(Setting("Others Color", Color.white()))
    private val hAlign = register(Setting("Horizontal Align", HAlign.Left))
    private val vAlign = register(Setting("Vertical Align", VAlign.Top))

    private val lines = mutableMapOf<String, Int>()

    private var oldWidth = 0
    private var oldHeight = 0

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        width = 0
        height = 0

        lines.clear()

        for(p in world.players) {
            if(range.value > 0 && p.distanceTo(player) > range.value) continue
            if(p == player) continue

            val text = p.getText()

            lines[text] = if(p.isFriend()) friendColor.value.rgb else neutralColor.value.rgb

            val textWidth = textRenderer.getWidth(text)

            if(textWidth > width) {
                width = textWidth
            }

            height += textRenderer.fontHeight
        }

        if(lines.isEmpty()) {
            width = 50
            height = textRenderer.fontHeight
        }

        when(hAlign.value) {
            HAlign.Left -> {}
            HAlign.Right -> if (width != oldWidth) x.value -= width - oldWidth
            HAlign.Center -> if (width != oldWidth) x.value -= (width - oldWidth) / 2
        }
        oldWidth = width

        when(vAlign.value) {
            VAlign.Top -> {}
            VAlign.Bottom -> if (height != oldHeight) y.value -= height - oldHeight
            VAlign.Middle -> if (height != oldHeight) y.value -= (height - oldHeight) / 2
        }
        oldHeight = height

        var textY = when(vAlign.value) {
            VAlign.Top, VAlign.Middle -> y.value.toFloat()
            VAlign.Bottom -> y.value + height.toFloat() - textRenderer.fontHeight
        }

        for((text, color) in lines) {
            val textWidth = textRenderer.getWidth(text)

            val textX = when(hAlign.value) {
                HAlign.Left -> x.value.toFloat()
                HAlign.Right -> x.value + width - textWidth.toFloat()
                HAlign.Center -> x.value + (width / 2) - (textWidth / 2f)
            }

            context.drawTextWithShadow(textRenderer, text, textX.toInt(), textY.toInt(), color)

            when(vAlign.value) {
                VAlign.Top, VAlign.Middle -> textY += textRenderer.fontHeight
                VAlign.Bottom -> textY -= textRenderer.fontHeight
            }
        }
    }

    private fun PlayerEntity.getText(): String {
        val distance = (if(this.y > player.y) "+" else if(this.y < player.y) "-" else "") + player.distanceTo(this).toInt()

        val hp = (this.health + this.absorptionAmount).toInt()
        val hpColor =
            if(hp <= 4) Formatting.DARK_RED
            else if(hp <= 8) Formatting.RED
            else if(hp <= 16) Formatting.YELLOW
            else if(hp <= 24) Formatting.GREEN
            else Formatting.DARK_GREEN

        val effects = (if(this.hasStatusEffect(StatusEffects.SPEED)) Formatting.AQUA + " [SP]"  else "") +
                (if(this.hasStatusEffect(StatusEffects.STRENGTH)) Formatting.RED + " [ST]" else "")

        return "[$distance]$effects ${Formatting.RESET}${this.name.string} $hpColor$hp"
    }

}