package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.util.math.MatrixStack

class RenderWorldEvent(val matrices: MatrixStack) : Event()