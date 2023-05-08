package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.player
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.util.math.MatrixStack
import org.joml.Quaternionf
import kotlin.math.PI

object Doll: Widget("Doll") {

    override var width = 0
        get() = (50 * scale.value).toInt()
    override var height = 0
        get() = (60 * scale.value).toInt()

    private val scale = register(Setting("Scale", 1.0, 0.5, 2.0))

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val quaternionf = Quaternionf().rotateZ(PI.toFloat())
        val quaternionf2 = Quaternionf().rotateX(player.pitch / 5 * (PI.toFloat() / 180))
        quaternionf.mul(quaternionf2)

        InventoryScreen.drawEntity(matrices, x.value + (width / 2), y.value + height, (30 * scale.value).toInt(), quaternionf, quaternionf2, player)
    }

}