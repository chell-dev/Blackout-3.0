package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import me.chell.blackout.mixin.accessors.MinecraftClientAccessor
import net.minecraft.item.Items
import net.minecraft.util.Hand

class XpBind: Feature("XP Bind", Category.Combat) {

    override val mainSetting = Setting("Bind", Bind.Toggle(onEnable = {onEnable()}, onDisable = {onDisable()}))

    private val armor = register(Setting("Stop on 100% durability", true))
    private val feet = register(Setting("Throw at feet", true))
    private val fast = register(Setting("Fast", true))

    private val accessor = mc as MinecraftClientAccessor

    private fun onEnable() {
        eventManager.register(this)
    }

    private fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if(armor.value && player.inventory.armor.none { it.isDamaged }) {
            mainSetting.value.enabled = false
            return
        }

        if(!fast.value && accessor.itemUseCooldown > 0) return

        if(player.inventory.mainHandStack.item == Items.EXPERIENCE_BOTTLE) {
            mc.interactionManager!!.interactItem(player, Hand.MAIN_HAND)
            if(!fast.value) accessor.itemUseCooldown = 4
        } else if(player.inventory.offHand[0].item == Items.EXPERIENCE_BOTTLE) {
            mc.interactionManager!!.interactItem(player, Hand.OFF_HAND)
            if(!fast.value) accessor.itemUseCooldown = 4
        } else {
            val currentSlot = player.inventory.selectedSlot
            val currentPitch = player.pitch

            for(i in 0 until 9) {
                if(player.inventory.getStack(i).item == Items.EXPERIENCE_BOTTLE) {
                    player.inventory.selectedSlot = i
                    if(feet.value) player.pitch = 90f
                    mc.interactionManager!!.interactItem(player, Hand.MAIN_HAND)
                    if(!fast.value) accessor.itemUseCooldown = 4
                    player.pitch = currentPitch
                    player.inventory.selectedSlot = currentSlot
                    return
                }
            }
        }

    }

}