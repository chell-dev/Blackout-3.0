package me.chell.blackout.impl.gui.newgui

import me.chell.blackout.api.util.*
import me.chell.blackout.impl.gui.newgui.util.Border
import me.chell.blackout.impl.gui.newgui.util.Rectangle
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f

class Tile(var title: String, val buttons: MutableList<Button>, x: Float, y: Float, width: Float, height: Float) {

    val background = Rectangle.Dynamic({NewGUI.window.x + x}, {NewGUI.window.y + y}, {width}, {height}, Color(0, 0, 0, 0))
    val header = Rectangle.Dynamic({NewGUI.window.x + x + tilePadding}, {NewGUI.window.y + y + tilePadding}, {width - (tilePadding*2)}, {headerHeight - (tilePadding*2)}, Color(20, 10, 20, 255))

    fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        header.border.position = Border.Position.Inside
        header.border.bottom.width = 0f
        header.color = Color.sync()
        headerHeight = 18f

        background.color = Color(0)
        background.border.position = Border.Position.Inside
        background.border.left.width = 1f
        background.border.left.color.sync = true
        background.border.right.width = 1f
        background.border.right.color.sync = true
        background.border.top.width = 1f
        background.border.top.color.sync = true
        background.border.bottom.width = 1f
        background.border.bottom.color.sync = true

        background.draw(matrices)
        header.draw(matrices)

        textRenderer.draw(matrices, title, false, header, HAlign.Left, VAlign.Middle, -1, padding = Vec2f(2f, 0f))

        for(button in buttons) {
            button.render(matrices, mouseX, mouseY, delta)
        }

    }

}