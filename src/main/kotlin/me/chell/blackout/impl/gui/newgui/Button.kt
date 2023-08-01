package me.chell.blackout.impl.gui.newgui

import me.chell.blackout.api.util.*
import me.chell.blackout.impl.gui.newgui.util.Border
import me.chell.blackout.impl.gui.newgui.util.Rectangle
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f

class Button(var title: String, parent: Tile, offset: Float) {

    private val background = Rectangle.Dynamic({parent.header.x}, {parent.header.y + parent.header.height + offset}, {parent.header.width}, {buttonHeight}, Color(0, 0, 0, 255))

    fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        tilePadding = 2f
        background.border.position = Border.Position.Inside
        background.border.bottom.width = 0f
        background.border.bottom.color.rainbow = true
        background.border.bottom.color.alpha = 0f

        background.border.left.width = 0f
        background.border.left.color.rgb = 0xff000000.toInt()

        buttonHeight = 15f

        background.color = Color(25, 25, 25)

        background.draw(matrices)

        textRenderer.draw(matrices, title, false, background, HAlign.Left, VAlign.Middle, -1, 0.9f, Vec2f(2f, 0f))
    }

}