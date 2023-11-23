package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleBindFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.attackEntity
import me.chell.blackout.api.util.isFriend
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.SwordItem

object KillAura: ToggleBindFeature("KillAura", Category.Combat) {

    override var description = "Attack entities around you"

    private val delay = Setting("Attack Delay", true)
    private val using = Setting("While Using Item", true)
    private val rotate = Setting("Rotate", true)
    private val crits = Setting("Criticals", false)

    private val players = Setting("Target Players", true)
    private val hostile = Setting("Target Hostile", false)
    private val passive = Setting("Target Passive", false)

    private val priority = Setting("Priority", Priority.Health)

    private val range = Setting("Range", 6.0, 3.0, 8.0)
    private val wallRange = Setting("Wall Range", 6.0, 3.0, 8.0, level = 2)

    private val weaponOnly = Setting("Sword/Axe Only", false)

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if(delay.value && player.getAttackCooldownProgress(0.0f) != 1f) return
        if(weaponOnly.value && (player.mainHandStack.item !is SwordItem && player.mainHandStack.item !is AxeItem)) return
        if(!using.value && player.isUsingItem) return

        val target = mc.world!!.entities
            .asSequence()
            .filter { player.distanceTo(it) <= if (player.canSee(it)) range.value else wallRange.value }
            .filter { (players.value && it is PlayerEntity) || (hostile.value && it is HostileEntity) || (passive.value && it is PassiveEntity) }
            .filter { it != player }
            .filter { if(it is PlayerEntity) !it.isFriend() else true }
            .filter { it as LivingEntity; !it.isDead && it.health > 0 }
            .minByOrNull {
                it as LivingEntity
                if (priority.value == Priority.Health) it.health + it.absorptionAmount
                else player.distanceTo(it)
            }

        if(target != null) {
            val ground = player.isOnGround
            val sprint = player.isSprinting

            if(crits.value && !player.isClimbing && !player.isTouchingWater && !player.hasStatusEffect(StatusEffects.BLINDNESS) && !player.hasVehicle()) {
                if(player.fallDistance <= 0.0f) player.fallDistance = 0.01f
                player.isOnGround = false
                player.isSprinting = false
            }

            player.attackEntity(target, rotate.value)

            player.isOnGround = ground
            player.isSprinting = sprint
        }
    }

    enum class Priority {
        Distance, Health
    }

}