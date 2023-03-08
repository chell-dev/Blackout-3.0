package me.chell.blackout.impl.features.render

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.ParticleEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.eventManager
import net.minecraft.client.particle.ExplosionLargeParticle
import net.minecraft.client.particle.ExplosionSmokeParticle

class NoRender: ToggleFeature("NoRender", Category.Render, false) {

    private val explosions = register(Setting("Explosion Particles", true))

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onParticle(event: ParticleEvent) {
        when(event.particle) {
            is ExplosionLargeParticle, is ExplosionSmokeParticle -> if(explosions.value) event.canceled = true
        }
    }

}