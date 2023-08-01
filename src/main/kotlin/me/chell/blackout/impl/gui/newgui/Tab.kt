package me.chell.blackout.impl.gui.newgui

import me.chell.blackout.api.util.*
import me.chell.blackout.impl.gui.newgui.util.Border
import me.chell.blackout.impl.gui.newgui.util.Rectangle
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f

open class Tab(val title: String, val offset: Int) {

    val rect = Rectangle(0f, 33f, 60f, 18f, Color(0xff151515.toInt()))

    private val selectedBorder = Border(Border.Position.Inside, Border.Line(1f, Color.sync()), Border.Line(1f, Color.sync()), Border.Line(1f, Color.sync()), Border.Line(0f, Color.sync()))
    private val unselectedBorder = Border(Border.Position.Inside, bottom = Border.Line(1f, Color.sync()))

    val selected get() = Navbar.selected == this

    fun renderTab(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        rect.border = if(selected) selectedBorder else unselectedBorder
        rect.x = NewGUI.window.x + 10f + (offset*rect.width)

        rect.draw(matrices)
        textRenderer.draw(matrices, title, false, rect, HAlign.Left, VAlign.Middle, -1, 0.8f, Vec2f(2f, 0f))
    }

    open fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {}

}