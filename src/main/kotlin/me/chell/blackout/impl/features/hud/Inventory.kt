package me.chell.blackout.impl.features.hud

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Color
import me.chell.blackout.api.util.modId
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

object Inventory: Widget("Inventory") {

    override var width = 144
    override var height = 48

    private val mode = Setting("Mode", Mode.Texture)
    private val color = Setting("Background", Color(0.5f, 0.5f, 0.5f, 0.5f))

    private val id = Identifier(modId, "textures/inventory.png")

    enum class Mode {
        Texture, Color
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        when(mode.value) {
            Mode.Texture -> {
                RenderSystem.setShaderTexture(0, id)
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                RenderSystem.enableBlend()
                RenderSystem.defaultBlendFunc()
                RenderSystem.enableDepthTest()
                context.drawTexture(id, x.value, y.value, 0f, 0f, width, height, width, height)
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            }
            Mode.Color -> {
                if(color.value.alpha > 0f) {
                    context.fill(x.value, y.value, x.value + width, y.value + height, color.value.rgb)
                }
            }
        }

        var itemX = x.value
        var itemY = y.value

        for(index in 9 until player.inventory.main.size) {
            val stack = player.inventory.main[index]
            //context.drawItem(stack, x.value, y.value)
            context.drawItemInSlot(textRenderer, stack, itemX, itemY)

            if((index-8) % 9 == 0) {
                itemX = x.value
                itemY += 16
            } else {
                itemX += 16
            }

        }

    }
}