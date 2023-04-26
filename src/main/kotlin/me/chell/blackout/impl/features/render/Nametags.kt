package me.chell.blackout.impl.features.render

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.RenderNametagEvent
import me.chell.blackout.api.events.RenderWorldEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Formatting
import net.minecraft.util.math.AffineTransformation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import kotlin.math.max

object Nametags: ToggleFeature("Nametags", Category.Render) {

    private val showGamemode = register(Setting("Show Gamemode", false))
    private val scale = register(Setting("Scale", 2.7f, 1f, 5f))
    private val friendColor = register(Setting("Friend Color", Color.sync()))

    private val custom = register(Setting("Customize", false))

    private val distanceScale = register(Setting("Distance Scale", 10, 1, 20, level = 2) {custom.value})
    private val normalColor = register(Setting("Neutral Color", Color(-1), level = 2) {custom.value})
    private val nameShadow = register(Setting("Shadow", true, level = 2) {custom.value})
    private val backgroundColor = register(Setting("Background Color", Color(0xaa252525.toInt()), level = 2) {custom.value})
    private val itemSize = register(Setting("Item Size", 13, 1, 20, level = 2) {custom.value})

    private val duraBar = register(Setting("Durability Bar", false, level = 2) {custom.value})
    private val duraValue = register(Setting("Durability Value", true, level = 2) {custom.value})
    private val duraScale = register(Setting("Durability Scale", 0.6f, 0.1f, 2f, level = 2) {custom.value && duraValue.value})
    private val duraShadow = register(Setting("Durability Shadow", true, level = 2) {custom.value && duraValue.value})

    private val overlayScale = register(Setting("Stack Size Scale", 0.7f, 0.1f, 2f, level = 2) {custom.value})
    private val stackColor = register(Setting("Stack Size Color", Color(-1), level = 2) {custom.value})
    private val stackShadow = register(Setting("Stack Size Shadow", true, level = 2) {custom.value})

    @EventHandler
    fun onRender(event: RenderWorldEvent) {
        for(target in world.players) {
            if(target == player) continue

            val camera = mc.gameRenderer.camera
            val matrixStack = RenderSystem.getModelViewStack()

            val pos = Vec3d(
                MathHelper.lerp(mc.tickDelta.toDouble(), target.lastRenderX, target.pos.x),
                MathHelper.lerp(mc.tickDelta.toDouble(), target.lastRenderY, target.pos.y) + target.height + 0.5,
                MathHelper.lerp(mc.tickDelta.toDouble(), target.lastRenderZ, target.pos.z)
            ).subtract(camera.pos)

            val gamemode = if(target.isCreative) " [C] " else if(target.isSpectator) " [SP] " else " [S] "
            val hp = (target.health + target.absorptionAmount).toInt()
            val color = if(target.isFriend()) friendColor.value.rgb else normalColor.value.rgb
            val string =  target.name.string + (if(showGamemode.value) gamemode else " ")
            val width = textRenderer.getWidth(string+hp).toFloat() / 2f
            val scale = (scale.value / 100f) * (max(distanceScale.value.toFloat(), player.distanceTo(target)) / distanceScale.value.toFloat())

            matrixStack.push()
            matrixStack.translate(pos.x, pos.y, pos.z)
            matrixStack.multiplyPositionMatrix(Matrix4f().rotation(camera.rotation))
            matrixStack.scale(scale, -scale, scale)
            RenderSystem.enableTexture()
            RenderSystem.disableDepthTest()
            RenderSystem.depthMask(true)
            matrixStack.scale(-1.0f, 1.0f, 1.0f)
            RenderSystem.applyModelViewMatrix()

            fill(AffineTransformation.identity().matrix, -width.toInt() - 1, -1, width.toInt(), textRenderer.fontHeight, backgroundColor.value.rgb)

            val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)

            val nameWidth = textRenderer.getWidth(string).toFloat()

            val hpColor =
                if(hp <= 4) Formatting.DARK_RED
                else if(hp <= 8) Formatting.RED
                else if(hp <= 16) Formatting.YELLOW
                else if(hp <= 24) Formatting.GREEN
                else Formatting.DARK_GREEN

            if(nameShadow.value) {
                textRenderer.draw(string, -width+0.5f, 0.5f, (color and 0xaaaaaa) shr 1, false, AffineTransformation.identity().matrix, immediate as VertexConsumerProvider, true, 0, 0xF000F0)
                immediate.draw()

                textRenderer.draw(hp.toString(), -width+nameWidth+0.5f, 0.5f, (hpColor.colorValue!! and 0xaaaaaa) shr 1, false, AffineTransformation.identity().matrix, immediate as VertexConsumerProvider, true, 0, 0xF000F0)
                immediate.draw()
            }

            textRenderer.draw(string, -width, 0.0f, color, false, AffineTransformation.identity().matrix, immediate as VertexConsumerProvider, true, 0, 0xF000F0)
            immediate.draw()

            textRenderer.draw(hp.toString(), -width+nameWidth, 0.0f, hpColor.colorValue!!, false, AffineTransformation.identity().matrix, immediate as VertexConsumerProvider, true, 0, 0xF000F0)
            immediate.draw()

            var itemX = -(itemSize.value*3)
            renderItem(target.mainHandStack, itemX, -itemSize.value-2, target)
            itemX += itemSize.value
            for(stack in target.armorItems.reversed()) {
                renderItem(stack, itemX, -itemSize.value-2, target, true)
                itemX += itemSize.value
            }
            renderItem(target.offHandStack, itemX, -itemSize.value-2, target)

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            RenderSystem.enableDepthTest()
            matrixStack.pop()
            RenderSystem.applyModelViewMatrix()
        }
    }

    @EventHandler
    fun onNametag(event: RenderNametagEvent) {
        event.canceled = true
    }

    private fun renderItem(stack: ItemStack, x: Int, y: Int, player: PlayerEntity, showDura: Boolean = false) {
        val bakedModel: BakedModel = mc.itemRenderer.getModel(stack, null, null, 0)
        mc.itemRenderer.zOffset = if (bakedModel.hasDepth()) mc.itemRenderer.zOffset + 50.0f else mc.itemRenderer.zOffset + 50.0f

        //mc.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false)
        //RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.push()

        matrixStack.translate(x.toFloat(), y.toFloat(), 0f)
        matrixStack.translate(itemSize.value/2f, itemSize.value/2f, 0.0f)
        matrixStack.scale(1.0f, -1.0f, 1.0f)
        matrixStack.scale(itemSize.value.toFloat(), itemSize.value.toFloat(), 1.0f)

        RenderSystem.applyModelViewMatrix()
        val matrixStack2 = MatrixStack()
        val entityVC = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val bl = !bakedModel.isSideLit
        if (bl) DiffuseLighting.disableGuiDepthLighting()
        mc.itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, entityVC, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, bakedModel)
        entityVC.draw()
        RenderSystem.enableDepthTest()
        if (bl) DiffuseLighting.enableGuiDepthLighting()
        matrixStack.pop()
        RenderSystem.applyModelViewMatrix()

        mc.itemRenderer.zOffset = if (bakedModel.hasDepth()) mc.itemRenderer.zOffset - 50.0f else mc.itemRenderer.zOffset - 50.0f

        if (stack.isEmpty) {
            return
        }
        if (stack.count != 1) {
            val matrixStack = RenderSystem.getModelViewStack()
            matrixStack.push()

            matrixStack.translate(x + itemSize.value.toFloat(), (y + itemSize.value.toFloat() - textRenderer.fontHeight) * overlayScale.value, 0.0f)
            matrixStack.scale(overlayScale.value, overlayScale.value, 1f)

            RenderSystem.applyModelViewMatrix()

            val color = stackColor.value.rgb

            val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
            if(stackShadow.value) {
                textRenderer.draw(stack.count.toString(),
                    (1.5f - textRenderer.getWidth(stack.count.toString())),
                    2.5f,
                    (color and 0xaaaaaa) shr 1, false, AffineTransformation.identity().matrix, immediate as VertexConsumerProvider, true, 0, 0xF000F0)
                immediate.draw()
            }

            textRenderer.draw(stack.count.toString(),
                (1 - textRenderer.getWidth(stack.count.toString())).toFloat(),
                2f,
                color, false, AffineTransformation.identity().matrix, immediate as VertexConsumerProvider, true, 0, 0xF000F0)
            immediate.draw()

            matrixStack.pop()
            RenderSystem.applyModelViewMatrix()
        }
        if (stack.isItemBarVisible && duraBar.value) {
            matrixStack.push()

            matrixStack.translate(x.toFloat() + 2f, y.toFloat() + itemSize.value - 2f, 0.0f)
            //matrixStack.scale(overlayScale.value, overlayScale.value, 1.0f)

            RenderSystem.applyModelViewMatrix()
            RenderSystem.disableDepthTest()
            RenderSystem.disableTexture()
            RenderSystem.disableBlend()

            fill(AffineTransformation.identity().matrix, 0, 0, itemSize.value - 2, 2, 0xff000000.toInt())
            fill(AffineTransformation.identity().matrix, 0f, 0f, (stack.itemBarStep / 13f) * (itemSize.value - 2), 1f, stack.itemBarColor, 1f)

            RenderSystem.enableBlend()
            RenderSystem.enableTexture()
            RenderSystem.enableDepthTest()
            matrixStack.pop()
            RenderSystem.applyModelViewMatrix()
        }
        if(duraValue.value && showDura && stack.isDamageable) {
            val matrixStack = RenderSystem.getModelViewStack()
            matrixStack.push()

            matrixStack.translate(x + (itemSize.value.toFloat() / 2f), (y - (textRenderer.fontHeight * duraScale.value)) + 1f, 0.0f)
            matrixStack.scale(duraScale.value, duraScale.value, 1f)

            RenderSystem.applyModelViewMatrix()

            val dura = ((stack.maxDamage - stack.damage) / stack.maxDamage.toFloat()) * 100

            val color = stack.itemBarColor
            val string = dura.toInt().toString()

            val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
            if(duraShadow.value) {
                textRenderer.draw(string,
                    (-textRenderer.getWidth(string) / 2f) + 0.5f,
                    0.5f,
                    (color and 0xaaaaaa) shr 1, false, AffineTransformation.identity().matrix, immediate as VertexConsumerProvider, true, 0, 0xF000F0)
                immediate.draw()
            }

            textRenderer.draw(string,
                -textRenderer.getWidth(string) / 2f,
                0f,
                color, false, AffineTransformation.identity().matrix, immediate as VertexConsumerProvider, true, 0, 0xF000F0)
            immediate.draw()

            matrixStack.pop()
            RenderSystem.applyModelViewMatrix()
        }
        val f = player.itemCooldownManager.getCooldownProgress(stack.item, mc.tickDelta)
        if (f > 0.0f) {
            RenderSystem.disableDepthTest()
            RenderSystem.disableTexture()
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            val ff = MathHelper.floor(itemSize.value * (1.0f - f))
            fill(AffineTransformation.identity().matrix, x, y + ff, x + itemSize.value, y + ff + MathHelper.ceil(itemSize.value * f), 0x7Fffffff)
            RenderSystem.enableTexture()
            RenderSystem.enableDepthTest()
        }
    }

    private fun fill(matrix: Matrix4f, x1: Int, y1: Int, x2: Int, y2: Int, color: Int, alpha: Float = -1f) =
        fill(matrix, x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), color, alpha)

    private fun fill(matrix: Matrix4f, x1: Float, y1: Float, x2: Float, y2: Float, color: Int, alpha: Float = -1f) {
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
        val f = if(alpha != -1f) alpha else (color shr 24 and 0xFF).toFloat() / 255.0f
        val g = (color shr 16 and 0xFF).toFloat() / 255.0f
        val h = (color shr 8 and 0xFF).toFloat() / 255.0f
        val j = (color and 0xFF).toFloat() / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0.0f).color(g, h, j, f).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0.0f).color(g, h, j, f).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0.0f).color(g, h, j, f).next()
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), 0.0f).color(g, h, j, f).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

}