package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.events.RenderWorldEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import me.chell.blackout.impl.features.hud.Cps
import net.minecraft.block.Blocks
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.*
import net.minecraft.world.Difficulty
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.ShapeType
import net.minecraft.world.explosion.Explosion
import java.util.function.Predicate
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class AutoCrystal: Feature("AutoCrystal", Category.Combat) {

    override val mainSetting = Setting("Enabled", Bind.Toggle(onEnable = {onEnable()}, onDisable = {onDisable()}))

    private val page = register(Setting("Settings", Page.Place))

    private val hitRange = register(Setting("Hit Range", 5.5, 0.0, 8.0) {page.value == Page.Hit})
    private val hitWallRange = register(Setting("Hit Wall Range", 5.5, 0.0, 8.0) {page.value == Page.Hit})
    private val hitRotate = register(Setting("Hit Rotate", true) {page.value == Page.Hit})
    private val hitDelay = register(Setting("Hit Speed", 19, 1, 20) {page.value == Page.Hit})

    private val placeRange = register(Setting("Place Range", 6.0, 0.0, 8.0) {page.value == Page.Place})
    private val placeWallRange = register(Setting("Place Wall Range", 6.0, 0.0, 8.0) {page.value == Page.Place})
    private val placeRotate = register(Setting("Place Rotate", true) {page.value == Page.Place})
    private val placeDelay = register(Setting("Place Speed", 20, 1, 20) {page.value == Page.Place})
    private val playerRange = register(Setting("Player Range", 12.0, 1.0, 20.0) {page.value == Page.Place})
    private val minDamage = register(Setting("Min Damage", 5.0, 0.0, 20.0) {page.value == Page.Place})
    private val maxSelfDamage = register(Setting("Max Self Damage", 8.0, 0.0, 20.0) {page.value == Page.Place})
    private val predict = register(Setting("Predict Multiplier", 3.0, 0.0, 5.0) {page.value == Page.Place})
    private val autoSwitch = register(Setting("Auto Switch", SwitchMode.Off) {page.value == Page.Place})
    private val noGappleSwitch = register(Setting("Don't Switch While Gappling", true) {page.value == Page.Place && autoSwitch.value != SwitchMode.Off})
    private val newPlacement = register(Setting("1.13+ Placement", true) {page.value == Page.Place})
    private val facePlaceHP = register(Setting("FacePlace HP", 8.0, 0.0, 20.0) {page.value == Page.Place})
    private val facePlaceArmor = register(Setting("FacePlace Armor%", 10, 0, 50) {page.value == Page.Place})
    private val facePlaceBind = register(Setting("FacePlace Bind", Bind.Toggle(onEnable={}, onDisable={})) {page.value == Page.Place})

    private val hitAndPlace = register(Setting("Hit & Place On The Same Tick", false) {page.value == Page.Other})
    private val espColor = register(Setting("ESP Color", Color.sync(0.5f)) {page.value == Page.Other})

    private var speedTicks = 0
    private var hitCounter = 0
    private var placeCounter = 0

    private var renderPos: BlockPos? = null

    private var tickCounter = 0
    private var crystalCounter = 0

    enum class Page {
        Place, Hit, Other
    }

    enum class SwitchMode {
        Off, Normal, Silent
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if(tickCounter == 20) {
            Cps.value = crystalCounter
            crystalCounter = 0
            tickCounter = 1
        }
        else tickCounter++

        if(speedTicks == 20) {
            speedTicks = 1
            hitCounter = 0
            placeCounter = 0
        } else speedTicks++

        place()
        hit()
    }

    @EventHandler
    fun onRender(event: RenderWorldEvent) {
        if(renderPos != null) drawBox(Box(renderPos), espColor.value)
    }

    private fun onEnable() {
        eventManager.register(this)
        renderPos = null
        speedTicks = 0
        hitCounter = 0
        placeCounter = 0

        tickCounter = 0
        crystalCounter = 0
    }

    private fun onDisable() {
        eventManager.unregister(this)
        renderPos = null

        tickCounter = 0
        crystalCounter = 0
    }

    private fun hit(): Boolean {
        if(hitRange.value <= 0) return false

        if(hitCounter >= hitDelay.value) return false

        var crystal = if(renderPos != null) world.getEntitiesByClass(EndCrystalEntity::class.java, Box(renderPos!!.up())){true}.firstOrNull() else null
        if(crystal == null) crystal = player.getClosestEntity(EndCrystalEntity::class.java, hitRange.value) as EndCrystalEntity? ?: return false

        val range = if (player.canSee(crystal)) hitRange.value else hitWallRange.value
        if (player.distanceTo(crystal) <= range) {
            player.attackEntity(crystal, hitRotate.value)
            hitCounter++
        }
        return true
    }

    private fun place() {
        if(placeRange.value <= 0) return

        if(placeCounter >= placeDelay.value) return

        var oldSlot = -1

        if(!player.isHolding(Items.END_CRYSTAL)) {
            when(autoSwitch.value) {
                SwitchMode.Off -> return
                SwitchMode.Normal -> {
                    val slot = player.inventory.findItemInHotbar(Items.END_CRYSTAL)
                    if(slot == -1 || (noGappleSwitch.value && player.isHolding(Items.ENCHANTED_GOLDEN_APPLE) && player.isUsingItem)) return
                    else player.inventory.selectedSlot = slot
                }
                SwitchMode.Silent -> {
                    val slot = player.inventory.findItemInHotbar(Items.END_CRYSTAL)
                    if(slot == -1 || (noGappleSwitch.value && player.isHolding(Items.ENCHANTED_GOLDEN_APPLE) && player.isUsingItem)) return
                    else {
                        oldSlot = player.inventory.selectedSlot
                        player.inventory.selectedSlot = slot
                    }
                }
            }
        }

        val target = player.getClosestEntity(PlayerEntity::class.java, playerRange.value){ it != player && !player.isFriend() } as PlayerEntity?
        if(target == null || player.distanceTo(target) > playerRange.value) return

        val range = max(placeRange.value, placeWallRange.value).toInt()

        val list = BlockPos.iterate(
            player.blockPos.x - range, player.blockPos.y - range, player.blockPos.z - range,
            player.blockPos.x + range, player.blockPos.y + range, player.blockPos.z + range)

        var placePos: BlockPos? = null
        var maxDamage = -1f

        val squaredRange = placeRange.value*placeRange.value
        val squaredWAllRange = placeWallRange.value*placeWallRange.value

        var raytrace: BlockHitResult? = null

        var isArmorLow = false
        if(facePlaceArmor.value > 0) {
            for (item in target.armorItems) {
                if(item.maxDamage == 0) continue
                if (item.damage / item.maxDamage * 100 <= facePlaceArmor.value) {
                    isArmorLow = true
                    break
                }
            }
        }

        val shouldFP = facePlaceBind.value.enabled
                || (facePlaceHP.value > 0.0 && target.health + target.absorptionAmount <= facePlaceHP.value)
                || (isArmorLow)

        val oldTargetPos = target.pos
        if(target != player && predict.value > 0 && target.speed > 0.01f) {
            target.setPosition(oldTargetPos.withBias(target.movementDirection, target.speed * predict.value))
        }

        for(block in list) {
            if(!block.canPlaceCrystal()) continue
            val ray = world.raycast(RaycastContext(player.eyePos, block.toCenterPos().add(0.0, 0.5, 0.0), ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player))
            val canRaytrace = ray.type == HitResult.Type.BLOCK && ray.blockPos == block

            val maxRange = if(canRaytrace) squaredRange else squaredWAllRange
            if(player.squaredDistanceTo(block.toCenterPos()) > maxRange) continue

            val damage = block.getExplosionDamage(target)
            if(!shouldFP && damage < minDamage.value) continue
            if(block.getExplosionDamage(player) > maxSelfDamage.value) continue
            if(damage > maxDamage) {
                placePos = block.mutableCopy()
                maxDamage = damage
                if(canRaytrace) raytrace = ray
            }
        }

        target.setPosition(oldTargetPos)

        renderPos = placePos

        placePos ?: return

        val yaw = player.yaw
        val pitch = player.pitch
        if(placeRotate.value)
            player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, placePos.toCenterPos().add(0.0, 0.5, 0.0))

        val hand = if(player.mainHandStack.item == Items.END_CRYSTAL) Hand.MAIN_HAND else Hand.OFF_HAND
        interactionManager.interactBlock(player, hand, raytrace ?: BlockHitResult(placePos.toCenterPos().add(0.0, 0.5, 0.0), Direction.UP, placePos, false))
        player.swingHand(hand)

        crystalCounter++

        player.yaw = yaw
        player.pitch = pitch

        placeCounter++

        if(oldSlot != -1) player.inventory.selectedSlot = oldSlot
    }

    private fun ClientPlayerEntity.getClosestEntity(entityClass: Class<out Entity>, range: Double, predicate: Predicate<Entity> = Predicate{true}): Entity? {
        val box = Box(pos.subtract(range, range, range), pos.add(range, range, range))
        return world.getEntitiesByClass(entityClass, box, predicate).minByOrNull { distanceTo(it) }
    }

    private fun BlockPos.canPlaceCrystal(): Boolean {
        val blockState = world.getBlockState(this)
        if(!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.BEDROCK)) return false

        val up = up()

        if(!world.isAir(up)) return false
        if(!newPlacement.value && !world.isAir(up(2))) return false

        if(world.getOtherEntities(null, Box(up.x.toDouble(), up.y.toDouble(), up.z.toDouble(), up.x + 1.0, up.y + 2.0, up.z + 1.0)).isNotEmpty()) return false // todo up.y + 1.0 ?\

        return true
    }

    private fun BlockPos.getExplosionDamage(target: LivingEntity): Float {
        val q = 12.0
        val vec3d = Vec3d(x + 0.5, y + 1.0, z + 0.5)
        val w = sqrt(target.squaredDistanceTo(vec3d)) / q
        val ac = (1.0 - w) * Explosion.getExposure(vec3d, target)

        var damage = ((ac * ac + ac) / 2.0 * 7.0 * q + 1.0).toInt().toFloat()

        when(world.difficulty) {
            Difficulty.PEACEFUL -> damage = 0f
            Difficulty.EASY -> damage = min(damage / 2.0f + 1.0f, damage)
            Difficulty.NORMAL -> {}
            Difficulty.HARD -> damage = damage * 3.0f / 2.0f
        }

        damage = DamageUtil.getDamageLeft(damage, target.armor.toFloat(), target.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).toFloat())

        if(target.hasStatusEffect(StatusEffects.RESISTANCE)) {
            damage = max((damage * (25 - ((target.getStatusEffect(StatusEffects.RESISTANCE)!!.amplifier + 1) * 5)).toFloat()) / 25.0f, 0.0f)
        }

        val i = EnchantmentHelper.getProtectionAmount(target.armorItems, DamageSource.explosion(null))
        if(i > 0) damage = DamageUtil.getInflictedDamage(damage, i.toFloat())

        //damage = max(damage - target.absorptionAmount, 0.0f)

        return damage
    }
}