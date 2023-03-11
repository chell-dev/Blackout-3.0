package me.chell.blackout.api.util

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.WorldRenderer
import net.minecraft.util.math.Box
import java.awt.Color

fun drawBox(box: Box, color: Color) {
    RenderSystem.enableBlend()

    val tessellator = Tessellator.getInstance()
    val bb = tessellator.buffer

    val box = box.offset(mc.gameRenderer.camera.pos.negate())

    bb.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR)
    WorldRenderer.drawBox(bb, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
    tessellator.draw()
}
