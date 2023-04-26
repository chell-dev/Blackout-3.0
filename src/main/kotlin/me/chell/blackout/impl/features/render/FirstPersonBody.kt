package me.chell.blackout.impl.features.render

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.RenderArmEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.mc
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen

object FirstPersonBody: ToggleFeature("First Person Body", Category.Render, false) {

    override var description = "See your own legs"

    fun isActive() = mainSetting.value && mc.options.perspective.isFirstPerson && mc.currentScreen !is AbstractInventoryScreen<*>

    override fun onEnable() {
        EventManager.register(this)
    }

    override fun onDisable() {
        EventManager.unregister(this)
    }

    @EventHandler
    fun onRenderViewModel(event: RenderArmEvent) {
        if(event.type != RenderArmEvent.Type.LeftItemEquip && event.type != RenderArmEvent.Type.RightItemEquip) event.canceled = true
    }

}