package me.chell.blackout.impl.gui

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.modId
import me.chell.blackout.api.util.modName
import me.chell.blackout.impl.features.client.GuiFeature
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ClientGUI: Screen(Text.literal("$modName GUI")) {

    private val bannerTexture = Identifier(modId, "textures/gui/banner.png")

    private val background: Int = Color(100, 100, 100, 150).rgb
    private val color = Color(161, 0, 255).rgb

    private val tabs = mutableListOf<CategoryTab>()
    var currentTab: CategoryTab

    private var animationTicks = 0
    private val animationLength = 5
    private var closing = false

    private val x = 0
    private val y = 0
    private val uiWidth = 300
    private val uiHeight get() = mc.window.scaledHeight
    val bannerHeight = 75

    init {
        var tabY = bannerHeight+1

        for(category in Category.values()) {
            tabs.add(CategoryTab(category, 0, tabY, this))
            tabY += CategoryTab.size
        }

        currentTab = tabs[0]
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        // animation linear interpolation
        val scissorWidth = if(animationTicks > 0) {
            val t = if (closing) animationTicks + delta else animationLength - animationTicks + delta
            (lerp(x.toFloat(), x + uiWidth.toFloat(), t) / animationLength).toInt()
        } else {
            uiWidth
        }

        enableScissor(x, y, x + scissorWidth, y + uiHeight)

        // background
        fill(matrices, x, y, x + uiWidth, y + uiHeight, background)

        // banner
        RenderSystem.setShaderTexture(0, bannerTexture)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        drawTexture(matrices, x, y, 0f, 0f, uiWidth, bannerHeight, uiWidth, bannerHeight)

        // line under banner
        drawHorizontalLine(matrices, x, x + uiWidth, bannerHeight, color)

        // tabs
        for(tab in tabs) {
            tab.render(matrices, mouseX, mouseY, delta)
        }

        // line next to icons
        drawVerticalLine(matrices, CategoryTab.size, bannerHeight, y + uiHeight, color)

        disableScissor()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(animationTicks > 0) return false

        for(tab in tabs) {
            if(tab.mouseClicked(mouseX, mouseY, button)) return true
        }

        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        for(tab in tabs) {
            if(tab.keyPressed(keyCode, scanCode, modifiers)) return true
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close()
            return true
        }
        return false
    }

    override fun init() {
        closing = false
        animationTicks = animationLength
        mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_IN, 1.0f, 1.0f))
    }

    override fun tick() {
        if(animationTicks > 0) {
            animationTicks--
            if(closing && animationTicks == 0) mc.setScreen(null)
        }
    }

    override fun close() {
        for(tab in tabs) {
            tab.onClose()
        }

        if(GuiFeature.instance.mainSetting.value.key.code == GLFW.GLFW_KEY_UNKNOWN)
            GuiFeature.instance.mainSetting.value.setKey(InputUtil.GLFW_KEY_BACKSLASH, InputUtil.Type.KEYSYM)

        closing = true
        animationTicks = animationLength
        mc.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_OUT, 1.0f, 1.0f))
    }

    override fun resize(client: MinecraftClient?, width: Int, height: Int) {
    }

    override fun shouldPause() = false

    private fun lerp(a: Float, b: Float, t: Float) = a * (1 - t) + b * t

    ////////////////////////////////////////////////////////////////////////////////////////

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        return super.charTyped(chr, modifiers)
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return super.isMouseOver(mouseX, mouseY)
    }

    override fun shouldCloseOnEsc(): Boolean {
        return super.shouldCloseOnEsc()
    }

    override fun clearAndInit() {
        super.clearAndInit()
    }

    override fun removed() {
        super.removed()
    }

    override fun renderBackground(matrices: MatrixStack?) {
        super.renderBackground(matrices)
    }



    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return super.keyReleased(keyCode, scanCode, modifiers)
    }
}