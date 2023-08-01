package me.chell.blackout.impl.gui.newgui.util

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.util.Color
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack

open class Rectangle(open var x: Float, open var y: Float, open var width: Float, open var height: Float, var color: Color, var border: Border = Border()) {

    fun contains(x: Number, y: Number) = x.toFloat() >= this.x && y.toFloat() >= this.y && x.toFloat() <= this.x + width && y.toFloat() <= this.y + height

    class Dynamic(private var xD: () -> Float, private var yD: () -> Float, private var widthD: () -> Float, private var heightD: () -> Float, color: Color): Rectangle(0f, 0f, 0f, 0f, color) {
        override var x
            get() = xD.invoke()
            set(value) {}

        override var y
            get() = yD.invoke()
            set(value) {}

        override var width
            get() = widthD.invoke()
            set(value) {}

        override var height
            get() = heightD.invoke()
            set(value) {}
    }

    fun draw(matrices: MatrixStack) {
        if(border.position == Border.Position.Inside)
            fill(matrices, x + border.left.width, y + border.top.width, x + width - border.right.width, y + height - border.bottom.width, color)
        else
            fill(matrices, x, y, x + width, y + height, color)

        drawBorder(matrices)
    }

    private fun drawBorder(matrices: MatrixStack) {
        when(border.position) {
            Border.Position.Inside -> {
                var left = x
                var right = x + width
                if(border.left.visible) fill(matrices, x, y, x + border.left.width, y + height, border.left.color).also { left += border.left.width }
                if(border.right.visible) fill(matrices, x + width - border.right.width, y, x + width, y + height, border.right.color).also { right -= border.right.width }
                if(border.top.visible) fill(matrices, left, y, right, y + border.top.width, border.top.color)
                if(border.bottom.visible) fill(matrices, left, y + height - border.bottom.width, right, y + height, border.bottom.color)
            }
            Border.Position.Outside -> {
                var left = x
                var right = x + width
                if(border.left.visible) fill(matrices, x - border.left.width, y, x, y + height, border.left.color).also { left -= border.left.width }
                if(border.right.visible) fill(matrices, x + width, y, x + width + border.right.width, y + height, border.right.color).also { right += border.right.width }
                if(border.top.visible) fill(matrices, left, y - border.top.width, right, y, border.top.color)
                if(border.bottom.visible) fill(matrices, left, y + height, right, y + height + border.bottom.width, border.bottom.color)
            }
            Border.Position.Middle -> {}
        }
    }

    private fun fill(matrices: MatrixStack, x1: Float, y1: Float, x2: Float, y2: Float, color: Color) {
        val matrix4f = matrices.peek().positionMatrix
        val bufferBuilder = Tessellator.getInstance().buffer
        val z = 0f

        val a = color.alpha
        val r = color.red
        val g = color.green
        val b = color.blue

        /*
        var i: Float
        var x1 = x1
        var x2 = x2
        var y1 = y1
        var y2 = y2

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
        */

        RenderSystem.enableBlend()
        RenderSystem.setShader(GameRenderer::getPositionColorProgram)
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix4f, x1, y1, z).color(r, g, b, a).next()
        bufferBuilder.vertex(matrix4f, x1, y2, z).color(r, g, b, a).next()
        bufferBuilder.vertex(matrix4f, x2, y2, z).color(r, g, b, a).next()
        bufferBuilder.vertex(matrix4f, x2, y1, z).color(r, g, b, a).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.disableBlend()
    }
}
