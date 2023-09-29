package me.chell.blackout.impl.features.render

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.RenderHudEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import kotlin.math.cos
import kotlin.math.sin

object Tracers: ToggleFeature("Tracers", Category.Render) {

    private val range = register(Setting("Player Range", 30, -1, 100, display = {if(it == -1) "Infinite" else "$it blocks"}))
    private val radiusSetting = register(Setting("Circle Radius", 20, 1, 30))
    private val size = register(Setting("Arrow Size", 20, 5, 50))
    private val enemyColor = register(Setting("Enemy Color", Color(1f, 0f, 0f, 0.5f)))
    private val friendColor = register(Setting("Friend Color", Color.sync(0f)))

    @EventHandler
    fun onRender2D(event: RenderHudEvent.Post) {
        for(target in world.players) {
            if(target == player) continue

            if(target.distanceTo(player) > range.value && range.value != -1) continue

            if(target.isFriend()) if(friendColor.value.alpha == 0f) continue
            else if(enemyColor.value.alpha == 0f) continue

            val d: Double = target.x - player.x
            val f: Double = target.z - player.z

            val angle = MathHelper.wrapDegrees(MathHelper.wrapDegrees(player.yaw) - MathHelper.wrapDegrees((MathHelper.atan2(f, d) * 57.2957763671875).toFloat()))
            val radians = Math.toRadians(angle.toDouble())

            val radius = radiusSetting.value * 10f
            val centerX = mc.window.scaledWidth / 2f
            val centerY = mc.window.scaledHeight / 2f

            val x = centerX + radius * -cos(radians)
            val y = centerY + radius * sin(radians)

            event.matrices.push()
            event.matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-MathHelper.wrapDegrees(angle + 90f)), x.toFloat(), y.toFloat(), 0f)
            drawTriangle(event.matrices, x.toFloat(), y.toFloat(), size.value.toFloat(), target.isFriend())
            //textRenderer.draw(event.matrices, a.toString(), x.toFloat(), y.toFloat(), -1)
            event.matrices.pop()
        }
    }

    private fun drawTriangle(matrices: MatrixStack, x: Float, y: Float, size: Float, isFriend: Boolean) {
        val color = if(isFriend) friendColor.value else enemyColor.value

        val x1 = x - (size/3f)
        val y1 = y + (size/2f)

        val x2 = x1 + (size/1.5f)
        val y2 = y1

        val x3 = x
        val y3 = y - (size/2f)

        val matrix4f = matrices.peek().positionMatrix
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix4f, x1, y1, 0f).color(color.red, color.green, color.blue, color.alpha).next()
        bufferBuilder.vertex(matrix4f, x2, y2, 0f).color(color.red, color.green, color.blue, color.alpha).next()
        bufferBuilder.vertex(matrix4f, x3, y3, 0f).color(color.red, color.green, color.blue, color.alpha).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.disableBlend()
    }

}