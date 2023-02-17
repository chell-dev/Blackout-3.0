package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.KeyPressedEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.interactionManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import me.chell.blackout.api.value.Value
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Items
import net.minecraft.util.Hand

class PearlBind: Feature("Pearl Bind", Category.Combat) {
    override val mainValue = Value("Activate", InputUtil.fromKeyCode(InputUtil.GLFW_KEY_R, -1))

    init {
        eventManager.register(this)
    }

    @EventHandler
    fun onKeyPressed(event: KeyPressedEvent) {
        if(event.key != mainValue.value || event.action != 1) return

        mc.player ?: return

        if(player.mainHandStack.item == Items.ENDER_PEARL) {
            val result = interactionManager.interactItem(player, Hand.MAIN_HAND)
            if(result.shouldSwingHand()) player.swingHand(Hand.MAIN_HAND)
            return
        }
        if(player.offHandStack.item == Items.ENDER_PEARL) {
            val result = interactionManager.interactItem(player, Hand.OFF_HAND)
            if(result.shouldSwingHand()) player.swingHand(Hand.OFF_HAND)
            return
        }

        val oldSlot = player.inventory.selectedSlot

        var pearlSlot = -1
        for(i in 0 until PlayerInventory.getHotbarSize()) {
            if(player.inventory.main[i].item == Items.ENDER_PEARL) {
                pearlSlot = i
                break
            }
        }

        if(pearlSlot == -1) return

        player.inventory.selectedSlot = pearlSlot

        val result = interactionManager.interactItem(player, Hand.MAIN_HAND)
        if(result.shouldSwingHand()) player.swingHand(Hand.MAIN_HAND)

        player.inventory.selectedSlot = oldSlot
    }
}