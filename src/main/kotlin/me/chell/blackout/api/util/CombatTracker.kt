package me.chell.blackout.api.util

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.AttackEntityEvent
import me.chell.blackout.api.events.DamageEntityEvent
import me.chell.blackout.api.events.EntityDamagedEvent
import me.chell.blackout.api.events.PlayerInteractBlockEvent
import me.chell.blackout.impl.features.client.Messages
import me.chell.blackout.impl.features.combat.AutoCrystal.getExplosionDamage
import net.minecraft.block.BedBlock
import net.minecraft.block.Blocks
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.dimension.DimensionTypes

/**
 * @author chell
 * @since 27.9.2023
 */
object CombatTracker {

    private const val timeout = 60 // 3 seconds

    private val targets = mutableMapOf<LivingEntity, Int>()

    // K/D
    val servers = mutableMapOf<String, IntArray>()

    @EventHandler
    fun onAttackEntity(event: AttackEntityEvent.Post) {
        if(event.target is LivingEntity) {
            targets[event.target] = timeout
        } else if(event.target is EndCrystalEntity) {
            val crystal = event.target
            for(entity in world.getOtherEntities(player, Box.of(crystal.pos, 12.0, 12.0, 12.0)) { it is LivingEntity }) {
                val damage = crystal.blockPos.getExplosionDamage(entity as LivingEntity, Vec3d.ZERO)
                if(damage > 0f) {
                    targets[entity] = timeout
                    EventManager.post(DamageEntityEvent(entity, damage))
                }
            }
        }
    }

    @EventHandler
    fun onInteractBlock(event: PlayerInteractBlockEvent) {
        if(event.blockHitResult.type == HitResult.Type.BLOCK) {
            val block = world.getBlockState(event.blockHitResult.blockPos).block
            val isOverworld = world.dimensionKey == DimensionTypes.OVERWORLD
            if(block is BedBlock && !isOverworld) {
                //todo
            } else if(block == Blocks.RESPAWN_ANCHOR && isOverworld) {
                //todo
            }
        }
    }

    @EventHandler
    fun onEntityDamaged(event: EntityDamagedEvent) {
        if(event.damageSource.attacker == player) {
            EventManager.post(DamageEntityEvent(event.entity, event.damageAmount))
        }

        if(event.entity.isDead || event.entity.health <= 0f) {
            val server = mc.currentServerEntry

            if (event.entity == player) {
                if (server != null) servers.getOrPut(server.address) { intArrayOf(0, 1) }[1]++
                Messages.onDeath()
            } else if ((targets.contains(event.entity) || event.damageSource.attacker == player)) {
                targets.remove(event.entity)
                if(event.entity is PlayerEntity) {
                    if (server != null) servers.getOrPut(server.address) { intArrayOf(1, 0) }[0]++
                    Messages.onKill(event.entity)
                }
            }
        }
    }

}