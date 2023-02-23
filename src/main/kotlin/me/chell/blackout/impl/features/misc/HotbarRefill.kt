package me.chell.blackout.impl.features.misc

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType

class HotbarRefill: ToggleFeature("Hotbar Refill", Category.Misc, false) {

    private val whitelist = Setting("Whitelist", mutableListOf(Items.EXPERIENCE_BOTTLE))

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        for(i in 0 until 9) {
            val stack = player.inventory.getStack(i)
            if(stack.count < stack.maxCount && whitelist.value.contains(stack.item)) {
                for(slot in 9 until player.inventory.main.size) {
                    if(player.inventory.getStack(slot).item == stack.item) {
                        mc.interactionManager!!.clickSlot(player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, player)
                        return
                    }
                }
            }
        }
    }

}