package me.chell.blackout.impl.features.render

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.ParticleEvent
import me.chell.blackout.api.events.RenderHudEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.eventManager
import net.minecraft.client.particle.ExplosionLargeParticle
import net.minecraft.client.particle.ExplosionSmokeParticle
import net.minecraft.client.particle.FireworksSparkParticle.FireworkParticle
import net.minecraft.client.particle.SpellParticle
import net.minecraft.client.particle.TotemParticle

class NoRender : ToggleFeature("NoRender", Category.Render, false) {

    private val explosions = register(Setting("Explosion Particles", false))
    private val potionParticles = register(Setting("Potion Particles", false))
    private val totemParticles = register(Setting("Totem Particles", false))
    private val fireworks = register(Setting("Firework Particles", false))
    private val hurtCam = register(Setting("Hurt Camera", false))
    private val fire = register(Setting("Fire Overlay", false))
    private val pumpkin = register(Setting("Pumpkin Overlay", false))
    private val snowOverlay = register(Setting("Snow Overlay", false))
    private val inWall = register(Setting("In Wall Overlay", false))
    private val portalOverlay = register(Setting("Portal Overlay", false))
    private val waterOverlay = register(Setting("Underwater Overlay", false))
    private val totemOverlay = register(Setting("Totem Overlay", false))
    private val tooltip = register(Setting("Item Tooltip", false))

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onParticle(event: ParticleEvent) {
        when (event.particle) {
            is ExplosionLargeParticle, is ExplosionSmokeParticle -> if (explosions.value) event.canceled = true
            is SpellParticle -> if (potionParticles.value) event.canceled = true
            is TotemParticle -> if (totemParticles.value) event.canceled = true
            is FireworkParticle -> if (fireworks.value) event.canceled = true
        }
    }

    @EventHandler
    fun onRenderHud(event: RenderHudEvent) {
        event.canceled = when (event) {
            is RenderHudEvent.Overlay -> (pumpkin.value && event.id.path == "textures/misc/pumpkinblur.png") || (snowOverlay.value && event.id.path == "textures/misc/powder_snow_outline.png")
            is RenderHudEvent.Portal -> portalOverlay.value
            is RenderHudEvent.Tooltip -> tooltip.value
            is RenderHudEvent.OnFire -> fire.value
            is RenderHudEvent.InWall -> inWall.value
            is RenderHudEvent.Underwater -> waterOverlay.value
            is RenderHudEvent.Totem -> totemOverlay.value
            is RenderHudEvent.Hurt -> hurtCam.value
            else -> false
        }

    }

}