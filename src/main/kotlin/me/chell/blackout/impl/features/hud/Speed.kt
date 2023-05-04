package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.TextWidget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.player
import net.minecraft.util.math.Vec3d

object Speed: TextWidget("Movement Speed", {
        when(Speed.units.value) {
            Units.MPS -> "${(Speed.speed * 10.0).toInt() / 10.0} m/s"
            Units.KMH -> "${(Speed.speed * 36.0).toInt() / 10.0} km/h"
            Units.MPH -> "${(Speed.speed * 22.36936).toInt() / 10.0} MPH"
        } }) {

    private var speed = 0.0
    private var lastPos = Vec3d.ZERO

    private val units = register(Setting("Units", Units.KMH, description = "Meters (Blocks) per second / Kilometers per hour / Miles per hour"))

    init {
        EventManager.register(this)
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