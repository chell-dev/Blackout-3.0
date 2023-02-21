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

class CategoryTab(val category: Category, var x: Int, var y: Int, private val parent: ClientGUI): DrawableHelper() {

    companion object {
        const val size = 50
    }

    private val margin = 5

    val features = mutableListOf<FeatureItem>()

    init {
        var bY = parent.bannerHeight+1+GuiItem.margin

        for(feature in featureManager.features) {
            if(feature.category == category) {
                features.add(FeatureItem(feature, size+1+GuiItem.margin, bY))
                bY += GuiItem.height + GuiItem.margin
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
            for(item in features) {
                item.render(matrices, mouseX, mouseY, delta)
            }
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(parent.currentTab != this && mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size) {
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

}