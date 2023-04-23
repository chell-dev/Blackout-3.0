package me.chell.blackout.impl.features.player

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.events.SoundEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.interactionManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d

class AutoFish : ToggleFeature("Auto Fish", Category.PLayer, false) {

    override var description = "Automatically recasts when you catch a fish"

    private val rotate = register(Setting("Rotate", false))

    private var interact = 0

    override fun onEnable() {
        eventManager.register(this)
    }

    override fun onDisable() {
        eventManager.unregister(this)
    }

    @EventHandler
    fun onSound(event: SoundEvent) {
        if (event.sound.id.path == "entity.fishing_bobber.splash" && mc.player != null && player.isHolding(Items.FISHING_ROD) && player.fishHook != null) {
            if (Vec3d(event.sound.x, event.sound.y, event.sound.z).distanceTo(player.fishHook!!.pos) < 1)
                interact = 2
        }
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if (interact > 0) {
            if (player.mainHandStack.item == Items.FISHING_ROD) {
                interactItem(Hand.MAIN_HAND)
            } else if (player.offHandStack.item == Items.FISHING_ROD) {
                interactItem(Hand.OFF_HAND)
            } else interact = 0
        }
    }

    private fun interactItem(hand: Hand) {
        val yaw = player.yaw
        val pitch = player.pitch
        if (player.fishHook != null && rotate.value) player.lookAt(
            EntityAnchorArgumentType.EntityAnchor.EYES,
            player.fishHook!!.pos
        )

        interactionManager.interactItem(player, hand)
        interact--

        player.yaw = yaw
        player.pitch = pitch
    }

}