package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.particle.Particle

data class ParticleEvent(val particle: Particle, var canceled: Boolean): Event()