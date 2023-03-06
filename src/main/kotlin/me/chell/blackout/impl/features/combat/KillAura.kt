package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.attackEntity
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.player
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.SwordItem

class KillAura: Feature("KillAura", Category.Combat) {

    override val mainSetting = Setting("Enabled", Bind.Toggle(onEnable = {onEnable()}, onDisable = {onDisable()}))

    private val delay = register(Setting("Attack Delay", true))
    private val using = register(Setting("While Using Item", true))
    private val rotate = register(Setting("Rotate", true))
    private val crits = register(Setting("Criticals", false))

    private val players = register(Setting("Target Players", true))
    private val hostile = register(Setting("Target Hostile", false))
    private val passive = register(Setting("Target Passive", false))

    private val priority = register(Setting("Priority", Priority.Health))

    private val range = register(Setting("Range", 6.0))
    private val wallRange = register(Setting("Wall Range", 6.0))

    private val weaponOnly = register(Setting("Sword/Axe Only", false))

    private fun onEnable() {
        eventManager.register(this)
    }

    private fun onDisable() {
        eventManager.unregister(this)
    }

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