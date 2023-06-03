package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.sound.SoundInstance

data class SoundEvent(val sound: SoundInstance): Event()