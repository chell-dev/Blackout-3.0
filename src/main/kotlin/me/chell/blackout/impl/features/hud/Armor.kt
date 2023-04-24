package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.util.math.MatrixStack

object Armor: Widget("Armor") {

    override var width = 64
    override var height = 16

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        var itemX = x.value
        for(stack in player.armorItems.reversed()) {
            mc.itemRenderer.renderInGui(stack, itemX, y.value)
            mc.itemRenderer.renderGuiItemOverlay(textRenderer, stack, itemX, y.value)
            itemX += 16
        }
    }
}