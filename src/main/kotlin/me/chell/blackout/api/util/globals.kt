package me.chell.blackout.api.util

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.world.ClientWorld

const val modId = "blackout"
val modName: String = FabricLoader.getInstance().getModContainer(modId).get().metadata.name
val modVersion: String = FabricLoader.getInstance().getModContainer(modId).get().metadata.version.friendlyString

val mc get() = MinecraftClient.getInstance()!!

val player: ClientPlayerEntity get() = mc.player!!

val world: ClientWorld get() = mc.world!!

val interactionManager: ClientPlayerInteractionManager get() = mc.interactionManager!!

val textRenderer: TextRenderer = mc.textRenderer