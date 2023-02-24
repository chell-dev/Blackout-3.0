package me.chell.blackout.impl.features.player

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.events.SoundEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import net.minecraft.item.Items
import net.minecraft.util.Hand

class AutoFish: ToggleFeature("Auto Fish", Category.PLayer, false) {

    private var interact = 0

    @EventHandler
    fun onSound(event: SoundEvent) {
        if(event.sound.id.path == "entity.fishing_bobber.splash")
            interact = 2
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if(interact > 0) {
            if(player.mainHandStack.item == Items.FISHING_ROD) {
                mc.interactionManager!!.interactItem(player, Hand.MAIN_HAND)
                interact--
            } else if(player.offHandStack.item == Items.FISHING_ROD) {
                mc.interactionManager!!.interactItem(player, Hand.OFF_HAND)
                interact--
            }
        }
    }

}