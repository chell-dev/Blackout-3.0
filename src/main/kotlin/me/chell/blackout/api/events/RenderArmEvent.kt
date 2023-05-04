package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.client.util.math.MatrixStack

open class RenderArmEvent(val type: Type, val matrices: MatrixStack, var equipProgress: Float, var canceled: Boolean, var fov: Double): Event() {

    enum class Type {
        LeftArm, RightArm, LeftItem, RightItem, LeftItemEquip, RightItemEquip, Fov
    }

}