package me.chell.blackout.impl.gui.buttons

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.*
import me.chell.blackout.impl.gui.Button
import me.chell.blackout.impl.gui.GuiItem
import me.chell.blackout.impl.gui.items.SettingItem
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// please don't look at this code it's awful but i'm going insane i'm never touching this again
class ColorButton(private val parent: GuiItem, private val setting: Setting<Color>, expandable: Boolean): Button(parent, expandable) {

    override var width = 32
    override val height get() = 16

    override val x get() = parent.x + parent.width - GuiItem.margin - width
    override val y get() = parent.y + (parent.height / 2) - (height / 2)

    private val parentHeight = if(parent is SettingItem) 26 else 28

    private var extended = false

    private var clicked = 0

    private val hueTexture = Identifier(modId, "textures/gui/hue.png")

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        RenderSystem.setShaderTexture(0, icon)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        drawTexture(matrices, parent.x + parent.width - GuiItem.margin - 32 - GuiItem.margin - iconSize, parent.y + GuiItem.margin, 0f, 0f, iconSize, iconSize, iconSize, iconSize)

        // Small Rectangle
        fill(matrices, parent.x + parent.width - GuiItem.margin - 32, parent.y + GuiItem.margin,
            parent.x + parent.width - GuiItem.margin, parent.y + GuiItem.margin + 16, setting.value.rgb)

        if(!extended) return

        when(clicked) {
            1 -> {
                val sbX = parent.x + parent.width - (GuiItem.margin*2) - 112
                val sbY = parent.y + 26 + GuiItem.margin

                val mX = min(max(sbX, mouseX), sbX + 100)
                val mY = min(max(sbY, mouseY), sbY + 100)

                val saturation = (mX - sbX) / 100f
                val brightness = 1 - ((mY - sbY) / 100f)

                val color = java.awt.Color.HSBtoRGB(getHue(setting.value), saturation, brightness)
                setting.value.red = color.red
                setting.value.green = color.green
                setting.value.blue = color.blue
            }
            2 -> {
                val hueY = parent.y + 26 + GuiItem.margin

                val mY = min(max(hueY, mouseY), hueY + 100)

                val hue = 1 - ((mY - hueY) / 100f)

                val array = FloatArray(3)
                val sb = java.awt.Color.RGBtoHSB((setting.value.red * 255).toInt(), (setting.value.green * 255).toInt(), (setting.value.blue * 255).toInt(), array)

                val color = java.awt.Color.HSBtoRGB(hue, sb[1], sb[2])
                setting.value.red = color.red
                setting.value.green = color.green
                setting.value.blue = color.blue
            }
            3 -> {
                val alphaX = parent.x + parent.width - (GuiItem.margin*2) - 112

                val mX = min(max(alphaX, mouseX), alphaX + 100)

                setting.value.alpha = (mX - alphaX) / 100f
            }
        }

        var itemX = parent.x + parent.width - GuiItem.margin - 12
        var itemY = parent.y + 26 + GuiItem.margin

        // Hue Slider
        drawHueSlider(matrices, itemX, itemY, 12, 100)
        itemX -= 100 + GuiItem.margin

        // Saturation/Brightness Picker
        drawSB(matrices, itemX.toFloat(), itemY.toFloat(), itemX + 100f, itemY + 100f)

        // Opacity Slider
        drawAlphaSlider(matrices, itemX.toFloat(), itemY.toFloat() + 100f + GuiItem.margin, itemX + 100f, itemY + 100f + GuiItem.margin + 12f)

        // RGB Text
        drawBorder(matrices, parent.x + GuiItem.margin, itemY, itemX - (parent.x + GuiItem.margin) - GuiItem.margin, 14)
        textRenderer.drawWithShadow(matrices, "RGB", parent.x + GuiItem.margin.toFloat() + 3, itemY.toFloat() + 3, -1)
        textRenderer.drawWithShadow(matrices, "${Formatting.GRAY}${Integer.toHexString(setting.value.rgb)}", itemX - GuiItem.margin - 3f - textRenderer.getWidth(Integer.toHexString(setting.value.rgb)), itemY.toFloat() + 3, -1)

        // Opacity Text
        itemY += 14 + GuiItem.margin
        drawBorder(matrices, parent.x + GuiItem.margin, itemY, itemX - (parent.x + GuiItem.margin) - GuiItem.margin, 14)
        textRenderer.drawWithShadow(matrices, "Opacity", parent.x + GuiItem.margin.toFloat() + 3, itemY.toFloat() + 3, -1)
        textRenderer.drawWithShadow(matrices, "${Formatting.GRAY}${(setting.value.alpha * 100).toInt()}%", itemX - GuiItem.margin - 3f - textRenderer.getWidth("${(setting.value.alpha * 100).toInt()}%"), itemY.toFloat() + 3, -1)

        // Rainbow Button
        itemY += 14 + GuiItem.margin + 20
        if(!setting.value.rainbow) drawBorder(matrices, parent.x + GuiItem.margin, itemY, itemX - (parent.x + GuiItem.margin) - GuiItem.margin, 14)
        else fill(matrices, parent.x + GuiItem.margin, itemY, itemX - GuiItem.margin, itemY + 14, Color(161, 0, 255).rgb)
        textRenderer.drawWithShadow(matrices, "Rainbow", parent.x + GuiItem.margin.toFloat() + 3, itemY.toFloat() + 3, -1)

        // Sync Button
        itemY += 14 + GuiItem.margin
        if(!setting.value.sync) drawBorder(matrices, parent.x + GuiItem.margin, itemY, itemX - (parent.x + GuiItem.margin) - GuiItem.margin, 14)
        else fill(matrices, parent.x + GuiItem.margin, itemY, itemX - GuiItem.margin, itemY + 14, Color(161, 0, 255).rgb)
        textRenderer.drawWithShadow(matrices, "Sync", parent.x + GuiItem.margin.toFloat() + 3, itemY.toFloat() + 3, -1)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button == 2 && mouseX >= parent.x && mouseY >= parent.y && mouseX <= parent.x + parent.width && mouseY <= parent.y + parentHeight) {
            if(extended) {
                extended = false
                parent.height = parentHeight
            } else {
                extended = true
                parent.height = 155
            }
            parent.tab.updateItems()
            return true
        }
        // yeah
        if(button == 0 && extended) {
            val pickerX = parent.x + parent.width - (GuiItem.margin*2) - 112
            val pickerY = parent.y + 26 + GuiItem.margin
            if(mouseX >= pickerX && mouseY >= pickerY) {
                if(mouseX >= pickerX + 100 + GuiItem.margin && mouseX <= pickerX + 112 + GuiItem.margin && mouseY <= pickerY + 100) {
                    // hue
                    clicked = 2
                    return true
                } else if(mouseX <= pickerX + 100) {
                    if(mouseY <= pickerY + 100) {
                        // sb
                        clicked = 1
                        return true
                    } else if(mouseY >= pickerY + 100 + GuiItem.margin && mouseY <= pickerY + 100 + GuiItem.margin + 12) {
                        // alpha
                        clicked = 3
                        return true
                    }
                }
            } else {
                if(mouseX >= parent.x + GuiItem.margin && mouseX <= pickerX - GuiItem.margin) {
                    val rainbowY = parent.y + 74 + (GuiItem.margin*3)
                    val syncY = parent.y + 88 + (GuiItem.margin*4)
                    if(mouseY >= rainbowY && mouseY <= rainbowY + 14) {
                        setting.value.rainbow = !setting.value.rainbow
                        return true
                    } else if(mouseY >= syncY && mouseY <= syncY + 14) {
                        setting.value.sync = !setting.value.sync
                        return true
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        clicked = 0
        return super.mouseReleased(mouseX, mouseY, button)
    }

    private fun drawBorder(matrices: MatrixStack?, x: Int, y: Int, width: Int, height: Int) {
        val border = 1
        val color = Color(161, 0, 255).rgb

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

        fill(matrices, x, y, x + width, y + border, color)
        fill(matrices, x + width - border, y + border, x + width, y + height, color)
        fill(matrices, x, y + height, x + width, y + height - border, color)
        fill(matrices, x, y + border, x + border, y + height, color)
    }

    private fun drawAlphaSlider(matrices: MatrixStack?, x1: Float, y1: Float, x2: Float, y2: Float) {
        val matrix = matrices!!.peek().positionMatrix
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        val tessellator = Tessellator.getInstance()
        val bb = tessellator.buffer

        val c = setting.value

        bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bb.vertex(matrix, x2, y1, 0f).color(c.red, c.green, c.blue, 1f).next()
        bb.vertex(matrix, x1, y1, 0f).color(c.red, c.green, c.blue, 0f).next()
        bb.vertex(matrix, x1, y2, 0f).color(c.red, c.green, c.blue, 0f).next()
        bb.vertex(matrix, x2, y2, 0f).color(c.red, c.green, c.blue, 1f).next()
        tessellator.draw()

        RenderSystem.disableBlend()
    }

    private fun drawHueSlider(matrices: MatrixStack?, x: Int, y: Int, width: Int, height: Int) {
        RenderSystem.setShaderTexture(0, hueTexture)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        drawTexture(matrices, x, y, 0f, 0f, width, height, width, height)
    }

    private fun drawSB(matrices: MatrixStack?, x1: Float, y1: Float, x2: Float, y2: Float) {
        val matrix = matrices!!.peek().positionMatrix
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        val tessellator = Tessellator.getInstance()
        val bb = tessellator.buffer

        val color = java.awt.Color.getHSBColor(getHue(setting.value), 1f, 1f)

        bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bb.vertex(matrix, x2, y1, 0f).color(color.red / 255f, color.green / 255f, color.blue / 255f, 1f).next()
        bb.vertex(matrix, x1, y1, 0f).color(1f, 1f, 1f, 1f).next()
        bb.vertex(matrix, x1, y2, 0f).color(1f, 1f, 1f, 1f).next()
        bb.vertex(matrix, x2, y2, 0f).color(color.red / 255f, color.green / 255f, color.blue / 255f, 1f).next()
        tessellator.draw()

        bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bb.vertex(matrix, x2, y1, 0f).color(0f, 0f, 0f, 0f).next()
        bb.vertex(matrix, x1, y1, 0f).color(0f, 0f, 0f, 0f).next()
        bb.vertex(matrix, x1, y2, 0f).color(0f, 0f, 0f, 1f).next()
        bb.vertex(matrix, x2, y2, 0f).color(0f, 0f, 0f, 1f).next()
        tessellator.draw()

        RenderSystem.disableBlend()
    }

    // https://stackoverflow.com/a/26233318
    private fun getHue(color: Color): Float {
        val red = color.red * 255
        val green = color.green * 255
        val blue = color.blue * 255

        val min = red.coerceAtMost(green).coerceAtMost(blue).toFloat()
        val max = red.coerceAtLeast(green).coerceAtLeast(blue).toFloat()
        if (min == max) {
            return 0f
        }
        var hue = when (max) {
            red.toFloat() -> {
                (green - blue) / (max - min)
            }
            green.toFloat() -> {
                2f + (blue - red) / (max - min)
            }
            else -> {
                4f + (red - green) / (max - min)
            }
        }
        hue *= 60
        if (hue < 0) hue += 360
        return hue.roundToInt() / 360f
    }

    // my descent into madness is complete
}