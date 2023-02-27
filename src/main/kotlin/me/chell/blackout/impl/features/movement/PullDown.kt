package me.chell.blackout.impl.features.movement

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import net.minecraft.text.Text
import net.minecraft.world.RaycastContext

class PullDown: ToggleFeature("PullDown", Category.Movement, false) {

    private val maxHeight = register(Setting("Max Height", 4.0))
    private val minHeight = register(Setting("Min Height", 0.1))

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if(!player.isOnGround || player.isTouchingWater) return

        val ray = mc.world!!.raycast(RaycastContext(player.pos, player.pos.add(0.0, -maxHeight.value - 1, 0.0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, player))

        val distance = player.pos.y - ray.pos.y
        if(distance >= minHeight.value && distance <= maxHeight.value) {
            val v = player.velocity
            player.setVelocity(v.x, -10.0, v.z)
        }
    }

}