package me.chell.blackout.impl.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.util.mc
import me.chell.blackout.impl.gui.items.FeatureItem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import kotlin.math.min

open class Tab(var x: Int, var y: Int, val parent: ClientGUI, val icon: Identifier) {

    companion object {
        const val size = 43
    }

    protected val margin = 5

    protected var scrollAmount = 0

    open val items = mutableListOf<GuiItem>()

    open fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float){
        RenderSystem.setShaderTexture(0, icon)
        if(parent.currentTab == this) RenderSystem.setShaderColor(161f/255f, 0f, 1f, 1f)
        else RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        context.drawTexture(icon, x+margin, y+margin, 0f, 0f, size -(margin*2), size -(margin*2), size -(margin*2), size -(margin*2))

        if(parent.currentTab == this) {
            context.enableScissor(size + 1, parent.bannerHeight + 1, x + parent.uiWidth, parent.descY.toInt())
            val mouse = mouseX >= size +1+ GuiItem.margin && mouseX <= parent.uiWidth - GuiItem.margin
            for(item in items) {
                item.render(context, mouseX, mouseY, delta)

                if(mouse && item is FeatureItem && mouseY >= item.y && mouseY <= item.y + item.height)
                    parent.hoveredItem = item.feature
            }
            context.disableScissor()
        }
    }

    open fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(parent.currentTab != this && mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size) {
            parent.currentTab.onClose()
            parent.currentTab = this
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            return true
        }

        if(parent.currentTab == this) {
            for(item in items) {
                if(item.mouseClicked(mouseX, mouseY, button)) return true
            }
        }

        return false
    }

    open fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(parent.currentTab == this) {
            for(item in items) {
                if(item.mouseReleased(mouseX, mouseY, button)) return true
            }
        }

        return false
    }

    open fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if(parent.currentTab == this) {
            for(item in items) {
                if(item.keyPressed(keyCode, scanCode, modifiers)) return true
            }
        }

        return false
    }

    open fun charTyped(chr: Char, modifiers: Int): Boolean {
        for(item in items) {
            if(item.charTyped(chr, modifiers)) return true
        }

        return false
    }

    open fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if(parent.currentTab == this) {
            scrollAmount += amount.toInt() * 10
            scrollAmount = min(200, scrollAmount)
            updateItems()
            return true
        }
        return false
    }

    open fun onClose() {
        for(item in items) {
            item.onClose()
        }
    }

    open fun updateItems() {
        var itemY = parent.bannerHeight+1+ GuiItem.margin + scrollAmount

        for(item in items) {
            item.y = itemY
            itemY += item.height + margin
        }
    }

}