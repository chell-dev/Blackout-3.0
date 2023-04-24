package me.chell.blackout.impl.features.render

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.events.RenderWorldEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import net.minecraft.block.Blocks
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

object HoleESP: ToggleFeature("Hole ESP", Category.Render, false) {

    private val range = register(Setting("Range", 10, 1, 20))
    private val height = register(Setting("Render Height", 1.0, -1.0, 1.0))
    private val mode = register(Setting("Render Mode", Mode.Gradient))
    private val obbyColor = register(Setting("Obsidian Color", Color(255, 0, 0, 100)))
    private val bedrockColor = register(Setting("Bedrock Color",  Color(0, 255, 0, 100)))

    private val obby = mutableListOf<Box>()
    private val bedrock = mutableListOf<Box>()

    enum class Mode {
        Box, Outline, OutlineBox, Gradient
    }

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onTick(event: PlayerTickEvent) {
        obby.clear()
        bedrock.clear()

        for(blockPos in BlockPos.iterate(
            player.blockPos.x - range.value, player.blockPos.y - range.value, player.blockPos.z - range.value,
            player.blockPos.x + range.value, player.blockPos.y + range.value, player.blockPos.z + range.value)) {

            if(player.squaredDistanceTo(blockPos.toCenterPos()) > range.value*range.value) continue

            when(getHole(blockPos)) {
                1 -> obby.add(Box(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), blockPos.x + 1.0, blockPos.y + height.value, blockPos.z + 1.0))
                2 -> bedrock.add(Box(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), blockPos.x + 1.0, blockPos.y + height.value, blockPos.z + 1.0))
            }
        }
    }

    @EventHandler
    fun onRender(event: RenderWorldEvent) {
        for(box in obby) {
            when(mode.value) {
                Mode.Box -> drawBox(box, obbyColor.value)
                Mode.Outline -> drawBoxOutline(box, obbyColor.value, 1f)
                Mode.OutlineBox -> { drawBox(box, obbyColor.value); drawBoxOutline(box, obbyColor.value, 1f)}
                Mode.Gradient -> drawGradient(box, obbyColor.value)
            }
        }

        for(box in bedrock) {
            when(mode.value) {
                Mode.Box -> drawBox(box, bedrockColor.value)
                Mode.Outline -> drawBoxOutline(box, bedrockColor.value, 1f)
                Mode.OutlineBox -> { drawBox(box, bedrockColor.value); drawBoxOutline(box, bedrockColor.value, 1f)}
                Mode.Gradient -> drawGradient(box, bedrockColor.value)
            }
        }
    }

    private fun drawGradient(box: Box, color: Color) {
        val r = color.red
        val g = color.green
        val b = color.blue
        val a = color.alpha

        val box = box.offset(mc.gameRenderer.camera.pos.negate())

        val x1 = box.minX
        val y1 = box.minY
        val z1 = box.minZ
        val x2 = box.maxX
        val y2 = box.maxY
        val z2 = box.maxZ

        RenderSystem.enableBlend()
        RenderSystem.disableDepthTest()

        val tessellator = Tessellator.getInstance()
        val bb = tessellator.buffer

        bb.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR)

        bb.vertex(x1, y1, z1).color(r, g, b, a).next()
        bb.vertex(x2, y1, z1).color(r, g, b, a).next()
        bb.vertex(x2, y2, z1).color(r, g, b, 0f).next()
        bb.vertex(x1, y2, z1).color(r, g, b, 0f).next()
        bb.vertex(x1, y1, z1).color(r, g, b, a).next()

        bb.vertex(x2, y1, z1).color(r, g, b, a).next()
        bb.vertex(x2, y1, z2).color(r, g, b, a).next()
        bb.vertex(x2, y2, z2).color(r, g, b, 0f).next()
        bb.vertex(x2, y2, z1).color(r, g, b, 0f).next()
        bb.vertex(x2, y1, z1).color(r, g, b, a).next()
        bb.vertex(x2, y1, z2).color(r, g, b, a).next()
        bb.vertex(x2, y1, z2).color(r, g, b, a).next()

        bb.vertex(x1, y1, z2).color(r, g, b, a).next()
        bb.vertex(x1, y2, z2).color(r, g, b, 0f).next()
        bb.vertex(x2, y2, z2).color(r, g, b, 0f).next()
        bb.vertex(x2, y1, z2).color(r, g, b, a).next()
        bb.vertex(x1, y1, z2).color(r, g, b, a).next()
        bb.vertex(x1, y1, z2).color(r, g, b, a).next()

        bb.vertex(x1, y1, z1).color(r, g, b, a).next()
        bb.vertex(x1, y2, z1).color(r, g, b, 0f).next()
        bb.vertex(x1, y2, z2).color(r, g, b, 0f).next()
        bb.vertex(x1, y1, z2).color(r, g, b, a).next()
        bb.vertex(x1, y1, z1).color(r, g, b, a).next()

        tessellator.draw()

        RenderSystem.enableDepthTest()
    }

    private fun getHole(blockPos: BlockPos): Int {
        var obby = false

        if(!world.getBlockState(blockPos).isAir || !world.getBlockState(blockPos.up()).isAir || !world.getBlockState(blockPos.up(2)).isAir)
            return 0

        val blocks = listOf(
            blockPos.north(),
            blockPos.south(),
            blockPos.west(),
            blockPos.east(),
            blockPos.down()
        )

        for(b in blocks) {
            val block = world.getBlockState(b).block
            if(block == Blocks.OBSIDIAN) obby = true
            else if(block != Blocks.BEDROCK) return 0
        }

        return if(obby) 1 else 2
    }

}