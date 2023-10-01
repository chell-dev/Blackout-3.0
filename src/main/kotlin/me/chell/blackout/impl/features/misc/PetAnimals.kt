package me.chell.blackout.impl.features.misc

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.world
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Hand
import org.lwjgl.glfw.GLFW

object PetAnimals: ToggleFeature("Petpetpet", Category.Misc) {

    override var description = "Pet any living entity."

    @EventHandler
    fun onInput(event: InputEvent.Mouse) {
        val entity = mc.targetedEntity

        if(event.action == 1 && event.key.code == GLFW.GLFW_MOUSE_BUTTON_2 && entity is LivingEntity && player.mainHandStack.isEmpty) {
            for (i in 0..6) {
                val d: Double = entity.random.nextGaussian() * 0.02
                val e: Double = entity.random.nextGaussian() * 0.02
                val f: Double = entity.random.nextGaussian() * 0.02
                world.addParticle(ParticleTypes.HEART, entity.getParticleX(1.0), entity.randomBodyY + 0.5, entity.getParticleZ(1.0), d, e, f)
            }
            player.swingHand(Hand.MAIN_HAND)
        }
    }

}