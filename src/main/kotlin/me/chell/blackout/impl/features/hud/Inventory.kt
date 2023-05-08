package me.chell.blackout.impl.features.hud

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

object Inventory: Widget("Inventory") {

    override var width = 144
    override var height = 48

    private val mode = register(Setting("Mode", Mode.Texture))
    private val color = register(Setting("Background", Color(0.5f, 0.5f, 0.5f, 0.5f)))

    private val id = Identifier(modId, "textures/inventory.png")

    enum class Mode {
        Texture, Color
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        when(mode.value) {
            Mode.Texture -> {
                RenderSystem.setShaderTexture(0, id)
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                RenderSystem.enableBlend()
                RenderSystem.defaultBlendFunc()
                RenderSystem.enableDepthTest()
                DrawableHelper.drawTexture(matrices, x.value, y.value, 0f, 0f, width, height, width, height)
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            }
            Mode.Color -> {
                if(color.value.alpha > 0f) {
                    DrawableHelper.fill(matrices, x.value, y.value, x.value + width, y.value + height, color.value.rgb)
                }
            }
        }

        var itemX = x.value
        var itemY = y.value

        for(index in 9 until player.inventory.main.size) {
            val stack = player.inventory.main[index]
            mc.itemRenderer.renderInGui(matrices, stack, itemX, itemY)
            mc.itemRenderer.renderGuiItemOverlay(matrices, textRenderer, stack, itemX, itemY)

            if((index-8) % 9 == 0) {
                itemX = x.value
                itemY += 16
            } else {
                itemX += 16
            }

        }

    }
}