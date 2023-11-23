package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.clickSlot
import me.chell.blackout.api.util.findItem
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.item.Items
import net.minecraft.item.PickaxeItem
import net.minecraft.item.SwordItem
import net.minecraft.screen.slot.SlotActionType

object Offhand: ToggleFeature("Offhand", Category.Combat) {

    private val gapple = Setting("Gapple", Bind.Toggle("Offhand Gapple", onEnable={}, onDisable={}))

    private val swordGapple = Setting("Right click gapple with Sword", false)
    private val pickaxeGapple = Setting("Right click gapple with Pickaxe", false)

    private val health = Setting("Totem HP", 16.0, 0.0, 40.0)

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        val item = if(player.health + player.absorptionAmount <= health.value) Items.TOTEM_OF_UNDYING
        else if(gapple.value.enabled || (mc.options.useKey.isPressed
                && ((swordGapple.value && player.mainHandStack.item is SwordItem) || (pickaxeGapple.value && player.mainHandStack.item is PickaxeItem))
                )) Items.ENCHANTED_GOLDEN_APPLE
        else Items.END_CRYSTAL

        if(player.offHandStack.item == item || mc.currentScreen is AbstractInventoryScreen<*>) return

        val slot = player.inventory.findItem(item)
        if(slot != -1) {
            clickSlot(slot, SlotActionType.PICKUP)
            clickSlot(45, SlotActionType.PICKUP)
            clickSlot(slot, SlotActionType.PICKUP)
        }
    }

}