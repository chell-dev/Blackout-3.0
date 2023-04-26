package me.chell.blackout.impl.features.render

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.RenderWorldEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box

object BlockHighlight: ToggleFeature("Block Highlight", Category.Render, false) {

    private val lineWidth = register(Setting("Outline Width", 1f, 0f, 10f))
    private val lineColor = register(Setting("Outline Color", Color.sync()) {lineWidth.value != 0f})
    private val fillColor = register(Setting("Fill Color", Color.sync(0.5f)))

    override fun onEnable() {
        EventManager.register(this)
    }

    override fun onDisable() {
        EventManager.unregister(this)
        mc.gameRenderer.setBlockOutlineEnabled(true)
    }

    @EventHandler
    fun onRender(event: RenderWorldEvent) {
        val target = mc.crosshairTarget ?: return
        if(target.type != HitResult.Type.BLOCK) return
        mc.gameRenderer.setBlockOutlineEnabled(false)
        val box = Box((target as BlockHitResult).blockPos)
        if(lineWidth.value != 0f) drawBoxOutline(box, lineColor.value, lineWidth.value)
        if(fillColor.value.alpha > 0f) drawBox(box, fillColor.value)
    }

}