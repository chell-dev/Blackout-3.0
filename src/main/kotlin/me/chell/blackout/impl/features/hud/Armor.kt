package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.gui.DrawContext

object Armor: Widget("Armor") {

    override var width = 64
    override var height = 16

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        var itemX = x.value
        for(stack in player.armorItems.reversed()) {
            //context.drawItem(stack, itemX, y.value)
            context.drawItemInSlot(textRenderer, stack, itemX, y.value)
            itemX += 16
        }
    }
}