package me.chell.blackout.impl.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.util.featureManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.impl.gui.items.FeatureItem
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import kotlin.math.max
import kotlin.math.min

class CategoryTab(val category: Category, var x: Int, var y: Int, private val parent: ClientGUI): DrawableHelper() {

    companion object {
        const val size = 50
    }

    private val margin = 5

    val features = mutableListOf<FeatureItem>()

    private var scrollAmount = 0

    init {
        var bY = parent.bannerHeight+1+GuiItem.margin

        for(feature in featureManager.features) {
            if(feature.category == category) {
                val item = FeatureItem(feature, size+1+GuiItem.margin, bY, this)
                features.add(item)
                bY += item.height + GuiItem.margin
            }
        }
    }

    fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float){
        RenderSystem.setShaderTexture(0, category.icon)
        if(parent.currentTab == this) RenderSystem.setShaderColor(161f/255f, 0f, 1f, 1f)
        else RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        drawTexture(matrices, x+margin, y+margin, 0f, 0f, size-(margin*2), size-(margin*2), size-(margin*2), size-(margin*2))

        if(parent.currentTab == this) {
            enableScissor(size + 1, parent.bannerHeight + 1, x + parent.uiWidth, parent.descY.toInt())
            for(item in features) {
                item.render(matrices, mouseX, mouseY, delta)
            }
            disableScissor()
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(parent.currentTab != this && mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size) {
            parent.currentTab.onClose()
            parent.currentTab = this
            mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
            return true
        }

        if(parent.currentTab == this) {
            for(item in features) {
                if(item.mouseClicked(mouseX, mouseY, button)) return true
            }
        }

        return false
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(parent.currentTab == this) {
            for(item in features) {
                if(item.mouseReleased(mouseX, mouseY, button)) return true
            }
        }

        return false
    }

    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if(parent.currentTab == this) {
            for(item in features) {
                if(item.keyPressed(keyCode, scanCode, modifiers)) return true
            }
        }

        return false
    }

    fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if(parent.currentTab == this) {
            scrollAmount += amount.toInt() * 10
            scrollAmount = min(200, scrollAmount)
            updateItems()
            return true
        }
        return false
    }

    fun onClose() {
        for(item in features) {
            item.onClose()
        }
    }

    fun updateItems() {
        var itemY = parent.bannerHeight+1+GuiItem.margin + scrollAmount

        for(item in features) {
            item.y = itemY
            itemY += item.fullHeight + margin
            item.updateItems()
        }
    }

}