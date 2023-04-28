package me.chell.blackout.api.util

import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.feature.FeatureManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.world.ClientWorld


const val modName = "Blackout"
const val modId = "blackout"
const val modVersion = "3.0"

val eventManager
    get() = EventManager

val featureManager
    get() = FeatureManager

val mc
    get() = MinecraftClient.getInstance()!!

val player: ClientPlayerEntity
    get() = mc.player!!

val world: ClientWorld
    get() = mc.world!!

val interactionManager: ClientPlayerInteractionManager
    get() = mc.interactionManager!!

val textRenderer: TextRenderer = mc.textRenderer