package me.chell.blackout.impl.features.render

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.RenderWorldEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.thrown.EnderPearlEntity
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity

object BoxEsp: ToggleFeature("ESP", Category.Render) {

    private val players = Setting("Players", false)
    private val playersOutline = Setting("Normal Outline", Color.white(), level = 2) {players.value}
    private val playersFill = Setting("Normal Fill", Color.white(0f), level = 2) {players.value}
    private val friendsOutline = Setting("Friend Outline", Color.sync(), level = 2) {players.value}
    private val friendsFill = Setting("Friend Fill", Color.white(0f), level = 2) {players.value}

    private val items = Setting("Items", false)
    private val itemsOutline = Setting("Item Outline", Color.white(), level = 2) {items.value}
    private val itemsFill = Setting("Item Fill", Color.white(0f), level = 2) {items.value}

    private val pearls = Setting("Ender Pearls", false)
    private val pearlsOutline = Setting("Pearl Outline", Color.white(), level = 2) {pearls.value}
    private val pearlsFill = Setting("Pearl Fill", Color.white(0f), level = 2) {pearls.value}

    private val xpBottles = Setting("XP Bottles", false)
    private val xpBottlesOutline = Setting("XP Outline", Color.white(), level = 2) {xpBottles.value}
    private val xpBottlesFill = Setting("XP Fill", Color.white(0f), level = 2) {xpBottles.value}

    @EventHandler
    fun onRender(event: RenderWorldEvent) {
        for(entity in world.entities) {
            when(entity) {
                is PlayerEntity -> {
                    if(players.value) {
                        if(entity == player) continue
                        if(entity.isFriend()) {
                            if(friendsFill.value.alpha > 0f) drawBox(entity.renderBoundingBox, friendsFill.value)
                            if(friendsOutline.value.alpha > 0f) drawBoxOutline(entity.renderBoundingBox, friendsOutline.value, 1f)
                        } else {
                            if(playersFill.value.alpha > 0f) drawBox(entity.renderBoundingBox, playersFill.value)
                            if(playersOutline.value.alpha > 0f) drawBoxOutline(entity.renderBoundingBox, playersOutline.value, 1f)
                        }
                    }
                }
                is ItemEntity -> {
                    if(items.value) {
                        if(itemsFill.value.alpha > 0f) drawBox(entity.renderBoundingBox, itemsFill.value)
                        if(itemsOutline.value.alpha > 0f) drawBoxOutline(entity.renderBoundingBox, itemsOutline.value, 1f)
                    }
                }
                is EnderPearlEntity -> {
                    if(pearls.value) {
                        if(pearlsFill.value.alpha > 0f) drawBox(entity.renderBoundingBox, pearlsFill.value)
                        if(pearlsOutline.value.alpha > 0f) drawBoxOutline(entity.renderBoundingBox, pearlsOutline.value, 1f)
                    }
                }
                is ExperienceBottleEntity -> {
                    if(xpBottles.value) {
                        if(xpBottlesFill.value.alpha > 0f) drawBox(entity.renderBoundingBox, xpBottlesFill.value)
                        if(xpBottlesOutline.value.alpha > 0f) drawBoxOutline(entity.renderBoundingBox, xpBottlesOutline.value, 1f)
                    }
                }
            }
        }
    }

}