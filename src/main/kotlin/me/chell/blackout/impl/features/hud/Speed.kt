package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d

object Speed: Widget("Movement Speed") {

    override var width = 10
    override var height = textRenderer.fontHeight

    private var speed = 0.0
    private var lastPos = Vec3d.ZERO

    private val units = register(Setting("Units", Units.KMH, description = "Meters (Blocks) per second / Kilometers per hour / Miles per hour"))

    init {
        eventManager.register(this)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val text = when(units.value) {
            Units.MPS -> "${(speed * 10.0).toInt() / 10.0} m/s"
            Units.KMH -> "${(speed * 36.0).toInt() / 10.0} km/h"
            Units.MPH -> "${(speed * 22.36936).toInt() / 10.0} MPH"
        }

        textRenderer.drawWithShadow(matrices, text, x.value.toFloat(), y.value.toFloat(), -1)
        width = textRenderer.getWidth(text)
    }

    @EventHandler
    fun onTick(event: PlayerTickEvent) {
        speed = player.pos.distanceTo(lastPos) * 20
        lastPos = player.pos
    }

    enum class Units {
        MPS, KMH, MPH
    }

}