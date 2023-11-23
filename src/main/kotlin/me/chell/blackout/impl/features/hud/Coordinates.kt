package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Color
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.plus
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Formatting
import net.minecraft.util.math.Direction
import net.minecraft.world.dimension.DimensionTypes

object Coordinates: Widget("Coordinates") {

    override var width = 10
    override var height = textRenderer.fontHeight * 2
    private val vAlign = Setting("Vertical Align", VAlign.UP)
    private val color = Setting("Color", Color.white())

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val x = (player.pos.x * 10).toInt() / 10.0
        val y = (player.pos.y * 10).toInt() / 10.0
        val z = (player.pos.z * 10).toInt() / 10.0
        val pos = player.blockPos

        val g = Formatting.GRAY
        val r = Formatting.RESET

        val color = color.value.rgb

        when(player.world.dimensionKey) {
            DimensionTypes.OVERWORLD -> {
                val a = "${g}X: ${r}$x${g}, Y: ${r}$y${g}, Z: ${r}$z $direction"
                val b = Formatting.GRAY + "X: ${pos.x / 8}, Y: ${pos.y}, Z: ${pos.z / 8}"
                width = maxOf(textRenderer.getWidth(a), textRenderer.getWidth(b), width)

                if(vAlign.value == VAlign.UP) {
                    context.drawTextWithShadow(textRenderer, b, this.x.value, this.y.value, color)
                    context.drawTextWithShadow(textRenderer, a, this.x.value, this.y.value + textRenderer.fontHeight, color)
                } else {
                    context.drawTextWithShadow(textRenderer, a, this.x.value, this.y.value, color)
                    context.drawTextWithShadow(textRenderer, b, this.x.value, this.y.value + textRenderer.fontHeight, color)
                }
            }
            DimensionTypes.THE_NETHER -> {
                val a = "${g}X: ${r}$x${g}, Y: ${r}$y${g}, Z: ${r}$z $direction"
                val b = Formatting.GRAY + "X: ${pos.x * 8}, Y: ${pos.y}, Z: ${pos.z * 8}"
                width = maxOf(textRenderer.getWidth(a), textRenderer.getWidth(b), width)

                if(vAlign.value == VAlign.UP) {
                    context.drawTextWithShadow(textRenderer, b, this.x.value, this.y.value, color)
                    context.drawTextWithShadow(textRenderer, a, this.x.value, this.y.value + textRenderer.fontHeight, color)
                } else {
                    context.drawTextWithShadow(textRenderer, a, this.x.value, this.y.value, color)
                    context.drawTextWithShadow(textRenderer, b, this.x.value, this.y.value + textRenderer.fontHeight, color)
                }
            }
            else -> {
                val a = "${g}X: ${r}$x${g}, Y: ${r}$y${g}, Z: ${r}$z $direction"
                width = maxOf(textRenderer.getWidth(a), width)

                if(vAlign.value == VAlign.UP) {
                    context.drawTextWithShadow(textRenderer, a, this.x.value, this.y.value + textRenderer.fontHeight, color)
                } else {
                    context.drawTextWithShadow(textRenderer, a, this.x.value, this.y.value, color)
                }
            }
        }
    }

    private val direction: String get() =
        "${Formatting.GRAY}[${Formatting.RESET}" +
                when(player.horizontalFacing) {
                    Direction.NORTH -> "-Z"
                    Direction.WEST -> "-X"
                    Direction.SOUTH -> "+Z"
                    Direction.EAST -> "+X"
                    else -> "" } +
        "${Formatting.GRAY}]${Formatting.RESET}"

    enum class VAlign {
        UP, DOWN
    }
}