package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.util.math.MatrixStack

class MainHandItem: Widget("Item Count - Mainhand") {

    override var width = 16
    override var height = 16

    private val stackableOnly = register(Setting("Only Stackable Items", true))

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val stack = player.mainHandStack
        if(!stack.isEmpty && (!stackableOnly.value || stack.isStackable)) {
            mc.itemRenderer.renderInGui(stack, x.value, y.value)
            mc.itemRenderer.renderGuiItemOverlay(textRenderer, stack, x.value, y.value, player.inventory.count(stack.item).toString())
        }
    }
}