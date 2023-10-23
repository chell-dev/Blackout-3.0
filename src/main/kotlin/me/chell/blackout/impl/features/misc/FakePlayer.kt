package me.chell.blackout.impl.features.misc

import com.mojang.authlib.GameProfile
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.entity.Entity
import java.util.*

object FakePlayer: ToggleFeature("Fake Player", Category.Misc) {

    override var description = "Spawns a fake player for testing and whatnot"

    private val uuid = UUID.randomUUID()
    private var id = -1

    override fun onEnable() {
        val world = mc.world ?: return
        mc.player ?: return

        val entity = OtherClientPlayerEntity(world, GameProfile(uuid, "FakePlayer"))
        entity.setPosition(player.pos)
        entity.inventory.clone(player.inventory)
        entity.attributes.setFrom(player.attributes)

        world.addEntity(entity)
    }

    override fun onDisable() {
        mc.world?.removeEntity(id, Entity.RemovalReason.DISCARDED)
        id = -1
    }
}