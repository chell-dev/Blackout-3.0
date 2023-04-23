package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.item.Items
import net.minecraft.item.PickaxeItem
import net.minecraft.item.SwordItem
import net.minecraft.screen.slot.SlotActionType

class Offhand : ToggleFeature("Offhand", Category.Combat, false) {

    private val gapple = register(Setting("Gapple", Bind.Toggle(onEnable = {}, onDisable = {})))

    private val swordGapple = register(Setting("Right click gapple with Sword", false))
    private val pickaxeGapple = register(Setting("Right click gapple with Pickaxe", false))

    private val health = register(Setting("Totem HP", 16.0, 0.0, 40.0))

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        val item = if (player.health + player.absorptionAmount <= health.value) Items.TOTEM_OF_UNDYING
        else if (gapple.value.enabled || (mc.options.useKey.isPressed
                    && ((swordGapple.value && player.mainHandStack.item is SwordItem) || (pickaxeGapple.value && player.mainHandStack.item is PickaxeItem))
                    )
        ) Items.ENCHANTED_GOLDEN_APPLE
        else Items.END_CRYSTAL

        if (player.offHandStack.item == item || mc.currentScreen is AbstractInventoryScreen<*>) return

        val slot = player.inventory.findItem(item)
        if (slot != -1) {
            clickSlot(slot, SlotActionType.PICKUP)
            clickSlot(45, SlotActionType.PICKUP)
            clickSlot(slot, SlotActionType.PICKUP)
        }
    }

}