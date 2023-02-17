package me.chell.blackout.api.util

import me.chell.blackout.Blackout
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager


const val modName = "Blackout"
const val modId = "blackout"
const val modVersion = "3.0"

val eventManager get() = Blackout.instance.eventManager

val mc get() = MinecraftClient.getInstance()!!

val player: ClientPlayerEntity get() = mc.player!!

val interactionManager: ClientPlayerInteractionManager get() = mc.interactionManager!!