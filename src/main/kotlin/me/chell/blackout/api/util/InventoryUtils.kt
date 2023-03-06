package me.chell.blackout.api.util

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.screen.slot.SlotActionType

fun PlayerInventory.findItem(item: Item): Int {
    for(i in 0 until size()) {
        if(getStack(i).item == item) return if(i < 9) i + 36 else i
    }
    return -1
}

fun PlayerInventory.findItemInHotbar(item: Item): Int {
    for(i in 0 until PlayerInventory.getHotbarSize()) {
        if(getStack(i).item == item) return i
    }
    return -1
}

fun clickSlot(slotId: Int, action: SlotActionType, button: Int = 0) {
    interactionManager.clickSlot(player.currentScreenHandler.syncId, slotId, button, action, player)
}