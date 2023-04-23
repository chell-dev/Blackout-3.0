package me.chell.blackout.api.util

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.WorldRenderer
import net.minecraft.entity.Entity
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper

fun drawBox(box: Box, color: Color) {
    RenderSystem.enableBlend()
    RenderSystem.disableDepthTest()

    val tessellator = Tessellator.getInstance()
    val bb = tessellator.buffer

    val newBox = box.offset(mc.gameRenderer.camera.pos.negate())

    bb.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR)
    WorldRenderer.drawBox(
        bb,
        newBox.minX,
        newBox.minY,
        newBox.minZ,
        newBox.maxX,
        newBox.maxY,
        newBox.maxZ,
        color.red,
        color.green,
        color.blue,
        color.alpha
    )
    tessellator.draw()

    RenderSystem.enableDepthTest()
}

fun drawBoxOutline(box: Box, color: Color, lineWidth: Float) {
    RenderSystem.enableBlend()
    RenderSystem.disableDepthTest()
    RenderSystem.lineWidth(lineWidth)

    val tessellator = Tessellator.getInstance()
    val bb = tessellator.buffer

    val r = color.red
    val g = color.green
    val b = color.blue

    val newBox = box.offset(mc.gameRenderer.camera.pos.negate())

    val x1 = newBox.minX
    val y1 = newBox.minY
    val z1 = newBox.minZ
    val x2 = newBox.maxX
    val y2 = newBox.maxY
    val z2 = newBox.maxZ

    bb.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)

    bb.vertex(x1, y1, z1).color(r, g, b, 1f).next()
    bb.vertex(x2, y1, z1).color(r, g, b, 1f).next()
    bb.vertex(x2, y1, z2).color(r, g, b, 1f).next()
    bb.vertex(x1, y1, z2).color(r, g, b, 1f).next()
    bb.vertex(x1, y1, z1).color(r, g, b, 1f).next()
    bb.vertex(x1, y2, z1).color(r, g, b, 1f).next()
    bb.vertex(x2, y2, z1).color(r, g, b, 1f).next()
    bb.vertex(x2, y1, z1).color(r, g, b, 1f).next()
    bb.vertex(x2, y2, z1).color(r, g, b, 1f).next()
    bb.vertex(x2, y2, z2).color(r, g, b, 1f).next()
    bb.vertex(x2, y1, z2).color(r, g, b, 1f).next()
    bb.vertex(x2, y2, z2).color(r, g, b, 1f).next()
    bb.vertex(x1, y2, z2).color(r, g, b, 1f).next()
    bb.vertex(x1, y1, z2).color(r, g, b, 1f).next()
    bb.vertex(x1, y2, z2).color(r, g, b, 1f).next()
    bb.vertex(x1, y2, z1).color(r, g, b, 1f).next()

    tessellator.draw()

    RenderSystem.lineWidth(1f)
    RenderSystem.enableDepthTest()
}

val Entity.renderBoundingBox: Box
    get() {
        val d = getDimensions(pose)
        val w = d.width / 2f

        val x = MathHelper.lerp(mc.tickDelta.toDouble(), lastRenderX, pos.x)
        val y = MathHelper.lerp(mc.tickDelta.toDouble(), lastRenderY, pos.y)
        val z = MathHelper.lerp(mc.tickDelta.toDouble(), lastRenderZ, pos.z)

        return Box(
            x - w, y, z - w,
            x + w, y + d.height, z + w
        )
    }
