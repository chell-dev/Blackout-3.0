package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.player
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

object Doll: Widget("Doll") {

    override var width = 0
        get() = (50 * scale.value).toInt()
    override var height = 0
        get() = (60 * scale.value).toInt()

    private val scale = Setting("Scale", 1.0, 0.5, 2.0)

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val quaternionf = Quaternionf().rotateZ(PI.toFloat())
        val quaternionf2 = Quaternionf().rotateX(player.pitch / 5 * (PI.toFloat() / 180))
        quaternionf.mul(quaternionf2)

        InventoryScreen.drawEntity(context, x.value + (width / 2f), y.value + height.toFloat(), (30 * scale.value).toInt(), Vector3f(0.0f, player.height / 2.0f + 0.0625f, 0.0f), quaternionf, quaternionf2, player)
    }

}