package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.gui.DrawContext

object OffHandItem: Widget("Item Count - Offhand") {

    override var width = 16
    override var height = 16

    private val stackableOnly = Setting("Only Stackable Items", true)

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val stack = player.offHandStack
        if(!stack.isEmpty && (!stackableOnly.value || stack.isStackable)) {
            //context.drawItem(stack, x.value, y.value)
            context.drawItemInSlot(textRenderer, stack, x.value, y.value, player.inventory.count(stack.item).toString())
        }
    }
}