package me.chell.blackout.impl.gui.newgui.util

import me.chell.blackout.api.util.Color

data class Border(var position: Position = Position.Outside,
                  var left: Line = Line(0f, Color.white()),
                  var right: Line = Line(0f, Color.white()),
                  var top: Line = Line(0f, Color.white()),
                  var bottom: Line = Line(0f, Color.white())) {

    class Line(width: Float, val color: Color) {
        var width: Float = width
            set(value) {
                if(value >= 0f) field = value
                else throw IllegalArgumentException("Border width cannot be negative.")
            }

        val visible get() = width > 0f && color.alpha > 0f
    }

    enum class Position {
        Inside, Outside, Middle
    }

}