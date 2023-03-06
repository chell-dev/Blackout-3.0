package me.chell.blackout.api.util

import me.chell.blackout.Blackout
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.world.ClientWorld


const val modName = "Blackout"
const val modId = "blackout"
const val modVersion = "3.0"

val eventManager get() = Blackout.instance.eventManager

val featureManager get() = Blackout.instance.featureManager

val mc get() = MinecraftClient.getInstance()!!

val player: ClientPlayerEntity get() = mc.player!!

val world: ClientWorld get() = mc.world!!

val interactionManager: ClientPlayerInteractionManager get() = mc.interactionManager!!