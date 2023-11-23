package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleBindFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.findItemInHotbar
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import me.chell.blackout.api.util.useItem
import me.chell.blackout.mixin.accessors.MinecraftClientAccessor
import net.minecraft.item.Items
import net.minecraft.util.Hand

object XpBind: ToggleBindFeature("XP Bind", Category.Combat) {

    override var description = "Throw XP bottles"

    private val armor = Setting("Stop on 100% durability", true)
    private val feet = Setting("Throw at feet", true)
    private val fast = Setting("Fast", true)

    private val accessor = mc as MinecraftClientAccessor

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if(armor.value && player.inventory.armor.none { it.isDamaged }) {
            mainSetting.value.enabled = false
            return
        }

        if(!fast.value && accessor.itemUseCooldown > 0) return

        if(player.inventory.mainHandStack.item == Items.EXPERIENCE_BOTTLE) {
            throwXp(Hand.MAIN_HAND)
        } else if(player.inventory.offHand[0].item == Items.EXPERIENCE_BOTTLE) {
            throwXp(Hand.OFF_HAND)
        } else {
            val currentSlot = player.inventory.selectedSlot

            val xp = player.inventory.findItemInHotbar(Items.EXPERIENCE_BOTTLE)
            if(xp != -1) {
                player.inventory.selectedSlot = xp
                throwXp(Hand.MAIN_HAND)
                player.inventory.selectedSlot = currentSlot
            }
        }

    }

    private fun throwXp(hand: Hand) {
        val currentPitch = player.pitch
        if(feet.value) player.pitch = 90f
        player.useItem(hand)
        if(!fast.value) accessor.itemUseCooldown = 4
        player.pitch = currentPitch
    }

}