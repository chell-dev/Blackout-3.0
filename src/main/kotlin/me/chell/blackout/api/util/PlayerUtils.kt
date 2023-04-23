package me.chell.blackout.api.util

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.entity.Entity
import net.minecraft.util.Hand

fun ClientPlayerEntity.useItem(hand: Hand) {
    if (interactionManager.interactItem(this, hand).shouldSwingHand()) swingHand(hand)
}

fun ClientPlayerEntity.attackEntity(target: Entity, rotate: Boolean) {
    val oldYaw = yaw
    val oldPitch = pitch
    if (rotate) player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.eyePos)

    interactionManager.attackEntity(this, target)
    swingHand(Hand.MAIN_HAND)

    yaw = oldYaw
    pitch = oldPitch
}