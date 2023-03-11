package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

abstract class RenderHudEvent: Event() {
    class Overlay(val id: Identifier, var canceled: Boolean): RenderHudEvent()
    class Tooltip(var canceled: Boolean): RenderHudEvent()
    class Portal(var canceled: Boolean): RenderHudEvent()
    class InWall(var canceled: Boolean): RenderHudEvent()
    class OnFire(var canceled: Boolean): RenderHudEvent()
    class Underwater(var canceled: Boolean): RenderHudEvent()
    class Totem(var canceled: Boolean): RenderHudEvent()
    class Hurt(var canceled: Boolean): RenderHudEvent()
    class Post(val matrices: MatrixStack, val tickDelta: Float): RenderHudEvent()
}