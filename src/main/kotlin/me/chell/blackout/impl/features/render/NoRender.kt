package me.chell.blackout.impl.features.render

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.ParticleEvent
import me.chell.blackout.api.events.RenderHudEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.player
import net.minecraft.client.particle.ExplosionLargeParticle
import net.minecraft.client.particle.ExplosionSmokeParticle
import net.minecraft.client.particle.FireworksSparkParticle.FireworkParticle
import net.minecraft.client.particle.SpellParticle
import net.minecraft.client.particle.TotemParticle

object NoRender: ToggleFeature("NoRender", Category.Render) {

    private val particles = register(Setting("Particles", null))
    private val explosions = register(Setting("Explosion", false, level = 2))
    private val potionParticles = register(Setting("Potion", false, level = 2))
    private val onlyOwnPotion = register(Setting("Only Self", false, level = 3) { potionParticles.value})
    private val totemParticles = register(Setting("Totem", false, level = 2))
    private val fireworks = register(Setting("Firework", false, level = 2))
    private val hurtCam = register(Setting("Hurt Camera", false))
    private val overlays = register(Setting("Overlays", null))
    private val fire = register(Setting("Fire", false, level = 2))
    private val pumpkin = register(Setting("Pumpkin", false, level = 2))
    private val snowOverlay = register(Setting("Snow", false, level = 2))
    private val inWall = register(Setting("In Wall", false, level = 2))
    private val portalOverlay = register(Setting("Portal", false, level = 2))
    private val waterOverlay = register(Setting("Underwater", false, level = 2))
    private val totemOverlay = register(Setting("Totem", false, level = 2))
    private val tooltip = register(Setting("Item Tooltip", false))

    @EventHandler
    fun onParticle(event: ParticleEvent) {
        when(event.particle) {
            is ExplosionLargeParticle, is ExplosionSmokeParticle -> if(explosions.value) event.canceled = true
            is SpellParticle -> if(potionParticles.value) {
                if(!onlyOwnPotion.value || player.squaredDistanceTo(event.particle.boundingBox.center) <= 4.0)
                    event.canceled = true
            }
            is TotemParticle -> if(totemParticles.value) event.canceled = true
            is FireworkParticle -> if(fireworks.value) event.canceled = true
        }
    }

    @EventHandler
    fun onRenderHud(event: RenderHudEvent) {
        event.canceled = when(event) {
            is RenderHudEvent.Overlay -> (pumpkin.value && event.id.path == "textures/misc/pumpkinblur.png") || (snowOverlay.value && event.id.path == "textures/misc/powder_snow_outline.png")
            is RenderHudEvent.Portal -> portalOverlay.value
            is RenderHudEvent.Tooltip -> tooltip.value
            is RenderHudEvent.OnFire -> fire.value
            is RenderHudEvent.InWall -> inWall.value
            is RenderHudEvent.Underwater -> waterOverlay.value
            is RenderHudEvent.Totem -> totemOverlay.value
            is RenderHudEvent.Hurt -> hurtCam.value
            else -> event.canceled
        }

    }

}