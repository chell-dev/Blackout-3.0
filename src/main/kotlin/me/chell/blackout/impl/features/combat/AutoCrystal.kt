package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerBreakBlockEvent
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.events.RenderWorldEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleBindFeature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import me.chell.blackout.impl.features.hud.Cps
import net.minecraft.block.Blocks
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.*
import net.minecraft.world.Difficulty
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.ShapeType
import java.time.Instant
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object AutoCrystal: ToggleBindFeature("AutoCrystal", Category.Combat) {

    private val page = Setting("Settings", Page.Place)

    private val hitRange = Setting("Hit Range", 5.5, 0.0, 8.0, "Hit range when the crystal is visible.", display = { if(it == 0.0) "Don't hit" else it.toString() }) {page.value == Page.Hit}
    private val hitWallRange = Setting("Hit Wall Range", 5.5, 0.0, 8.0, "Hit range when the crystal is behind a wall.", level = 2) {page.value == Page.Hit}
    private val hitRotate = Setting("Hit Rotate", true) {page.value == Page.Hit}
    private val hitDelay = Setting("Hit Delay", 50, 10, 250, "Crystal hit delay in milliseconds.", display = {"${it}ms"}) {page.value == Page.Hit}

    private val placeRange = Setting("Place Range", 6.0, 0.0, 8.0, "Place range when the block is visible.", display = { if(it == 0.0) "Don't place" else it.toString() }) {page.value == Page.Place}
    private val placeWallRange = Setting("Place Wall Range", 6.0, 0.0, 8.0, "Place range when the block is behind a wall.", level = 2) {page.value == Page.Place}
    private val placeRotate = Setting("Place Rotate", true) {page.value == Page.Place}
    private val placeDelay = Setting("Place Delay", 50, 10, 250, "Crystal place delay in milliseconds.", display = {"${it}ms"}) {page.value == Page.Place}
    private val wait = Setting("Await", true, description = "Prevent multiplacing.") {page.value == Page.Place}
    private val playerRange = Setting("Player Range", 12.0, 1.0, 20.0) {page.value == Page.Place}
    private val minDamage = Setting("Min Damage", 5.0, 0.0, 20.0) {page.value == Page.Place}
    private val maxSelfDamage = Setting("Max Self Damage", 8.0, 0.0, 20.0) {page.value == Page.Place}
    private val predict = Setting("Predict Movement", true) {page.value == Page.Place}
    private val predictHorizontal = Setting("Horizontal Multiplier", 2.0, 0.0, 5.0, level = 2, display = { "${it}x" }) {page.value == Page.Place && predict.value}
    private val predictVertical = Setting("Vertical Multiplier", 0.0, 0.0, 5.0, level = 2, display = { "${it}x" }) {page.value == Page.Place && predict.value}
    private val antiSurround = Setting("Anti Surround", AntiSurround.Off) {page.value == Page.Place}
    private val autoSwitch = Setting("Auto Switch", SwitchMode.Off) {page.value == Page.Place}
    private val noGappleSwitch = Setting("Anti Gapple Switch", true, description = "Don't auto switch while you're eating a golden apple.", level = 2) {page.value == Page.Place && autoSwitch.value != SwitchMode.Off}
    private val newPlacement = Setting("1.13+ Placement", true, description = "${Formatting.GREEN}ON:${Formatting.RESET} Place in 1 block tall spaces\n${Formatting.RED}OFF:${Formatting.RESET} Require 2 blocks of vertical space (for minecraft 1.12.2 and older).") {page.value == Page.Place}
    private val facePlaceHP = Setting("FacePlace HP", 8.0, 0.0, 20.0, "Health threshold to ignore min damage.", display = { if(it == 0.0) "Disabled" else it.toString() }) {page.value == Page.Place}
    private val facePlaceArmor = Setting("FacePlace Armor%", 10, 0, 50, "Armor percentage threshold to ignore min damage.", display = { if(it == 0) "Disabled" else it.toString() }) {page.value == Page.Place}
    private val facePlaceBind = Setting("FacePlace Bind", Bind.Toggle("FacePlace", onEnable={}, onDisable={}), description = "Keybind to ignore min damage.") {page.value == Page.Place}

    private val espColor = Setting("ESP Color", Color.sync(0.5f)) {page.value == Page.Other}
    private val espBox = Setting("Highlight Target", true) {page.value == Page.Other}

    private var renderPos: BlockPos? = null
    private var renderTicks = 0

    private var renderBB: Box? = null

    private var tickCounter = 0
    private var crystalCounter = 0

    private var placeMillis = 0L
    private var hitMillis = 0L

    private var queuedPlace: BlockPos? = null

    enum class Page {
        Place, Hit, Other
    }

    enum class SwitchMode {
        Off, Normal, Silent
    }

    enum class AntiSurround {
        Pre, Post, Off
    }

    init {
        Thread {
            while(true) {
                Thread.sleep(5)
                if(!mainSetting.value.enabled || mc.player == null || placeRange.value <= 0.0) continue

                val now = Instant.now().toEpochMilli()

                if(now - placeMillis >= placeDelay.value && queuedPlace == null)
                    if(place()) placeMillis = now
            }
        }.start()

        Thread {
            while(true) {
                Thread.sleep(5)
                if(!mainSetting.value.enabled || mc.player == null || hitRange.value <= 0.0) continue

                val now = Instant.now().toEpochMilli()

                if(now - hitMillis >= hitDelay.value)
                    if(hit()) hitMillis = now
            }
        }.start()
    }

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if(renderTicks <= 0) {
            renderPos = null
            renderBB = null
        }
        else renderTicks--

        if(tickCounter == 20) {
            Cps.value = crystalCounter
            crystalCounter = 0
            tickCounter = 1
        }
        else tickCounter++

        if(queuedPlace != null) {
            val blockPos = queuedPlace!!
            val down = blockPos.down()

            if(down.canPlaceCrystal() && getOtherEntities(player, Box(blockPos.x.toDouble() - 2.0, blockPos.y.toDouble(), blockPos.z.toDouble() - 2.0, blockPos.x + 3.0, blockPos.y + 1.0, blockPos.z + 3.0)).isNotEmpty()) {
                val squaredRange = placeRange.value*placeRange.value
                val squaredWAllRange = placeWallRange.value*placeWallRange.value

                val ray = world.raycast(RaycastContext(player.eyePos, down.toCenterPos().add(0.0, 0.5, 0.0), ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player))
                val canRaytrace = ray.type == HitResult.Type.BLOCK && ray.blockPos == down

                val maxRange = if(canRaytrace) squaredRange else squaredWAllRange
                if(player.squaredDistanceTo(down.toCenterPos().add(0.0, 0.5, 0.0)) > maxRange) {
                    queuedPlace = null
                    return
                }

                renderPos = down
                renderTicks = 10

                val yaw = player.yaw
                val pitch = player.pitch
                if(placeRotate.value)
                    player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, down.toCenterPos().add(0.0, 0.5, 0.0))

                val hand = if(player.mainHandStack.item == Items.END_CRYSTAL) Hand.MAIN_HAND else Hand.OFF_HAND
                val result = interactionManager.interactBlock(player, hand, if(canRaytrace) ray else BlockHitResult(down.toCenterPos().add(0.0, 0.5, 0.0), Direction.UP, down, false))
                if(result.shouldSwingHand()) player.swingHand(hand)

                crystalCounter++

                player.yaw = yaw
                player.pitch = pitch

                placeMillis = Instant.now().toEpochMilli()
            }

            queuedPlace = null
        }
    }

    @EventHandler
    fun onRender(event: RenderWorldEvent) {
        if(renderTicks > 0) {
            val a = espColor.value.alpha
            espColor.value.alpha = a * (renderTicks / 10f)

            if(renderPos != null) drawBox(Box(renderPos), espColor.value)
            if(renderBB != null && espBox.value) drawBoxOutline(renderBB!!, espColor.value, 1f)

            espColor.value.alpha = a
        }
    }

    override fun onEnable() {
        super.onEnable()

        renderPos = null
        renderBB = null
        renderTicks = 0

        tickCounter = 0
        crystalCounter = 0

        val now = Instant.now().toEpochMilli()
        placeMillis = now
        hitMillis = now
    }

    override fun onDisable() {
        super.onDisable()

        tickCounter = 0
        crystalCounter = 0
    }

    @EventHandler
    fun onBreakBlock(event: PlayerBreakBlockEvent) {
        when(antiSurround.value) {
            AntiSurround.Pre -> if(event.phase == PlayerBreakBlockEvent.Phase.Pre) queuedPlace = event.blockPos.toImmutable()
            AntiSurround.Post -> if(event.phase == PlayerBreakBlockEvent.Phase.Post) queuedPlace = event.blockPos.toImmutable()
            AntiSurround.Off -> {}
        }
    }

    private fun hit(): Boolean {
        var crystal = if(renderPos != null) world.entities.asSequence().filter{it is EndCrystalEntity}.filter{it.squaredDistanceTo(renderPos!!.up().toCenterPos()) <= 1.0}.firstOrNull() else null
        if(crystal == null) crystal = getClosestCrystal() ?: return false

        val range = if (player.canSee(crystal)) hitRange.value else hitWallRange.value
        if (player.distanceTo(crystal) <= range) {
            player.attackEntity(crystal, hitRotate.value)
        }
        return true
    }

    private fun place(): Boolean {
        if(renderPos != null && wait.value) {
            if(world.entities.asSequence().filter{it is EndCrystalEntity}.filter{it.squaredDistanceTo(renderPos!!.up().toCenterPos()) <= 1.0}.any()) return false
        }

        var oldSlot = -1

        if(!player.isHolding(Items.END_CRYSTAL)) {
            when(autoSwitch.value) {
                SwitchMode.Off -> return false
                SwitchMode.Normal -> {
                    val slot = player.inventory.findItemInHotbar(Items.END_CRYSTAL)
                    if(slot == -1 || (noGappleSwitch.value && player.isHolding(Items.ENCHANTED_GOLDEN_APPLE) && player.isUsingItem)) return false
                    else player.inventory.selectedSlot = slot
                }
                SwitchMode.Silent -> {
                    val slot = player.inventory.findItemInHotbar(Items.END_CRYSTAL)
                    if(slot == -1 || (noGappleSwitch.value && player.isHolding(Items.ENCHANTED_GOLDEN_APPLE) && player.isUsingItem)) return false
                    else {
                        oldSlot = player.inventory.selectedSlot
                        player.inventory.selectedSlot = slot
                    }
                }
            }
        }

        val target = getClosestPlayer() ?: return false

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

        var vec3d = Vec3d.ZERO
        if(predict.value) {
            val x = target.x - target.prevX
            val y = target.y - target.prevY
            val z = target.z - target.prevZ
            vec3d = Vec3d(x * predictHorizontal.value * 10.0, y * predictVertical.value * 10.0, z * predictVertical.value * 10.0)
        }

        for(block in list) {
            if(!block.canPlaceCrystal()) continue
            val ray = world.raycast(RaycastContext(player.eyePos, block.toCenterPos().add(0.0, 0.5, 0.0), ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player))
            val canRaytrace = ray.type == HitResult.Type.BLOCK && ray.blockPos == block

            val maxRange = if(canRaytrace) squaredRange else squaredWAllRange
            if(player.squaredDistanceTo(block.toCenterPos()) > maxRange) continue

            val damage = block.getExplosionDamage(target, vec3d)
            if(!shouldFP && damage < minDamage.value) continue
            if(block.getExplosionDamage(player, Vec3d.ZERO) > maxSelfDamage.value) continue
            if(damage > maxDamage) {
                placePos = block.mutableCopy()
                maxDamage = damage
                if(canRaytrace) raytrace = ray
            }
        }

        placePos ?: return false

        renderPos = placePos
        renderBB = target.boundingBox.offset(vec3d)
        renderTicks = 10

        val yaw = player.yaw
        val pitch = player.pitch
        if(placeRotate.value)
            player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, placePos.toCenterPos().add(0.0, 0.5, 0.0))

        val hand = if(player.mainHandStack.item == Items.END_CRYSTAL) Hand.MAIN_HAND else Hand.OFF_HAND
        val result = interactionManager.interactBlock(player, hand, raytrace ?: BlockHitResult(placePos.toCenterPos().add(0.0, 0.5, 0.0), Direction.UP, placePos, false))
        if(result.shouldSwingHand()) player.swingHand(hand)

        crystalCounter++

        player.yaw = yaw
        player.pitch = pitch

        if(oldSlot != -1) player.inventory.selectedSlot = oldSlot

        return true
    }

    private fun getClosestPlayer(): PlayerEntity? {
        return world.players.asSequence()
            .filterNotNull()
            .filter { player.distanceTo(it) <= playerRange.value  }
            .filter { !it.isFriend() }
            .filter { it.isAlive }
            .filter { !it.isCreative && !it.isSpectator }
            .filter { it != player }
            .minByOrNull { player.distanceTo(it) }
    }

    private fun getClosestCrystal(): EndCrystalEntity? {
        return world.entities.asSequence()
            .filterIsInstance<EndCrystalEntity>()
            .filter { player.distanceTo(it) <= if(player.canSee(it)) hitRange.value else hitWallRange.value  }
            .filter { it.isAlive }
            .minByOrNull { player.distanceTo(it) }
    }

    private fun getOtherEntities(except: Entity?, box: Box): List<Entity> {
        val list = world.entities.asSequence().filterNotNull().filter { box.intersects(it.boundingBox) }
        return if(except == null) list.toList()
        else list.filter { it != except }.toList()
    }

    private fun BlockPos.canPlaceCrystal(): Boolean {
        val blockState = world.getBlockState(this)
        if(!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.BEDROCK)) return false

        val up = up()

        if(!world.isAir(up)) return false
        if(!newPlacement.value && !world.isAir(up(2))) return false

        if(getOtherEntities(null, Box(up.x.toDouble(), up.y.toDouble(), up.z.toDouble(), up.x + 1.0, up.y + 2.0, up.z + 1.0)).isNotEmpty()) return false // todo up.y + 1.0 ?\

        return true
    }

    fun BlockPos.getExplosionDamage(target: LivingEntity, offset: Vec3d): Float {
        val q = 12.0
        val vec3d = Vec3d(x + 0.5, y + 1.0, z + 0.5)
        val w = sqrt(target.pos.add(offset).squaredDistanceTo(vec3d)) / q
        val ac = (1.0 - w) * getExplosionExposure(vec3d, target, target.boundingBox.offset(offset))

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

        val i = EnchantmentHelper.getProtectionAmount(target.armorItems, world.damageSources.explosion(null))
        if(i > 0) damage = DamageUtil.getInflictedDamage(damage, i.toFloat())

        //damage = max(damage - target.absorptionAmount, 0.0f)

        return damage
    }

    // Explosion.getExposure()
    private fun getExplosionExposure(source: Vec3d, entity: Entity, box: Box): Float {
        val d: Double = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0)
        val e: Double = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0)
        val f: Double = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0)
        val g = (1.0 - floor(1.0 / d) * d) / 2.0
        val h = (1.0 - floor(1.0 / f) * f) / 2.0

        if (d < 0.0 || e < 0.0 || f < 0.0) {
            return 0.0f
        }

        var i = 0
        var j = 0

        var k = 0.0
        while(k <= 1.0) {
            k += d

            var l = 0.0
            while(l <= 1.0) {
                l += e

                var m = 0.0
                while(m <= 1.0) {
                    m += f

                    val vec3d = Vec3d(MathHelper.lerp(k, box.minX, box.maxX) + g, MathHelper.lerp(l, box.minY, box.maxY), MathHelper.lerp(m, box.minZ, box.maxZ) + h)

                    if (entity.world.raycast(RaycastContext(vec3d, source, ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).type == HitResult.Type.MISS) {
                        ++i
                    }
                    ++j
                }
            }
        }

        return i.toFloat() / j.toFloat()
    }
}