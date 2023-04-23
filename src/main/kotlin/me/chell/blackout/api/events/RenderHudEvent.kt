package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

abstract class RenderHudEvent(var canceled: Boolean) : Event() {
    class Overlay(val id: Identifier, canceled: Boolean) : RenderHudEvent(canceled)
    class Tooltip(canceled: Boolean) : RenderHudEvent(canceled)
    class Portal(canceled: Boolean) : RenderHudEvent(canceled)
    class InWall(canceled: Boolean) : RenderHudEvent(canceled)
    class OnFire(canceled: Boolean) : RenderHudEvent(canceled)
    class Underwater(canceled: Boolean) : RenderHudEvent(canceled)
    class Totem(canceled: Boolean) : RenderHudEvent(canceled)
    class Hurt(canceled: Boolean) : RenderHudEvent(canceled)
    class Post(val matrices: MatrixStack, val tickDelta: Float) : RenderHudEvent(false)
}