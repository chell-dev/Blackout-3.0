package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.findItemInHotbar
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.useItem
import net.minecraft.item.Items
import net.minecraft.util.Hand

class PearlBind : Feature("Pearl Bind", Category.Combat) {

    override var description = "Keybind to throw an Ender Pearl from your hotbar"

    override val mainSetting = Setting("Activate", Bind.Action(action = { activate() }))

    private fun activate() {
        if (player.mainHandStack.item == Items.ENDER_PEARL) {
            player.useItem(Hand.MAIN_HAND)
            return
        }
        if (player.offHandStack.item == Items.ENDER_PEARL) {
            player.useItem(Hand.OFF_HAND)
            return
        }

        val oldSlot = player.inventory.selectedSlot
        val pearlSlot = player.inventory.findItemInHotbar(Items.ENDER_PEARL)

        if (pearlSlot == -1) return

        player.inventory.selectedSlot = pearlSlot
        player.useItem(Hand.MAIN_HAND)
        player.inventory.selectedSlot = oldSlot
    }
}