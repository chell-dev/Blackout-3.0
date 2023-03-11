package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.util.math.MatrixStack

abstract class RenderHudEvent(val matrices: MatrixStack, val tickDelta: Float): Event() {
    class Post(matrices: MatrixStack, tickDelta: Float): RenderHudEvent(matrices, tickDelta)
}