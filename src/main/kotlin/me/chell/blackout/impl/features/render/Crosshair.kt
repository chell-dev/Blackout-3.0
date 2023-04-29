package me.chell.blackout.impl.features.render

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.RenderHudEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Color
import me.chell.blackout.api.util.mc
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack

object Crosshair: ToggleFeature("Crosshair", Category.Render) {

    private val color = register(Setting("Color", Color.sync()))
    private val width = register(Setting("Width", 1f, 0f, 5f))
    private val length = register(Setting("Length", 5f, 0f, 10f))
    private val gap = register(Setting("Gap", 1f, 0f, 5f))
    private val tShape = register(Setting("T Shape", false))
    private val dot = register(Setting("Dot", false))

    @EventHandler
    fun onRenderCrosshair(event: RenderHudEvent.Crosshair) {
        event.canceled = true

        if(color.value.alpha == 0f) return
        val rgb = color.value.rgb
        val x = mc.window.scaledWidth / 2f - 0.5f
        val y = mc.window.scaledHeight / 2f
        val w = (width.value / 2f)

        if(dot.value) fill(event.matrices, x - w, y - w, x + w, y + w, rgb)

        if(width.value == 0f || length.value == 0f) return

        if(!tShape.value) fill(event.matrices, x - w, y - gap.value - length.value, x + w, y - gap.value, rgb) // top
        fill(event.matrices, x - gap.value - length.value, y - w, x - gap.value, y + w, rgb) // left
        fill(event.matrices, x + gap.value, y - w, x + gap.value + length.value, y + w, rgb) // right
        fill(event.matrices, x - w, y + gap.value, x + w, y + gap.value + length.value, rgb) // bottom
    }

    private fun fill(matrices: MatrixStack, x1: Float, y1: Float, x2: Float, y2: Float, color: Int) {
        val matrix = matrices.peek().positionMatrix
        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        var i: Float
        if (x1 < x2) {
            i = x1
            x1 = x2
            x2 = i
        }
        if (y1 < y2) {
            i = y1
            y1 = y2
            y2 = i
        }
        val f = (color shr 24 and 0xFF).toFloat() / 255.0f
        val g = (color shr 16 and 0xFF).toFloat() / 255.0f
        val h = (color shr 8 and 0xFF).toFloat() / 255.0f
        val j = (color and 0xFF).toFloat() / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1, y2, 0.0f).color(g, h, j, f).next()
        bufferBuilder.vertex(matrix, x2, y2, 0.0f).color(g, h, j, f).next()
        bufferBuilder.vertex(matrix, x2, y1, 0.0f).color(g, h, j, f).next()
        bufferBuilder.vertex(matrix, x1, y1, 0.0f).color(g, h, j, f).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

}