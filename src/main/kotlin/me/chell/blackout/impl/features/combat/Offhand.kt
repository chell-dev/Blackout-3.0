package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import net.minecraft.item.Items
import net.minecraft.item.PickaxeItem
import net.minecraft.item.SwordItem
import net.minecraft.screen.slot.SlotActionType

class Offhand: ToggleFeature("Offhand", Category.Combat, false) {

    private val gapple = Setting("Gapple", Bind.Toggle(onEnable={}, onDisable={}))

    private val swordGapple = Setting("Right click gapple with Sword", false)
    private val pickaxeGapple = Setting("Right click gapple with Pickaxe", false)

    private val health = Setting("Totem HP", 16.0)

    private var slot = -1
    private var step = 1

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        val item =
            if(player.health + player.absorptionAmount <= health.value) Items.TOTEM_OF_UNDYING
        else if(gapple.value.enabled || (mc.options.useKey.isPressed
                && ((swordGapple.value && player.mainHandStack.item is SwordItem) || (pickaxeGapple.value && player.mainHandStack.item is PickaxeItem))
                )) Items.ENCHANTED_GOLDEN_APPLE
        else Items.END_CRYSTAL

        if(player.offHandStack.item == item) return

        when(step) {
            1 -> {
                for(i in 0 until player.inventory.main.size) {
                    if(player.inventory.getStack(slot).item == item) {
                        mc.interactionManager!!.clickSlot(player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP_ALL, player)
                        slot = i
                        step++
                        break
                    }
                }
            }
            2 -> {
                mc.interactionManager!!.clickSlot(player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP_ALL, player)
                step++
            }
            3 -> {
                mc.interactionManager!!.clickSlot(player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP_ALL, player)
                step = 1
            }
        }
    }

}