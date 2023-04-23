package me.chell.blackout.api.util

import me.chell.blackout.impl.features.client.ColorsFeature

class Color(
    red: Float,
    green: Float,
    blue: Float,
    var alpha: Float = 1f,
    var rainbow: Boolean = false,
    sync: Boolean = false
) {
    constructor(red: Int, green: Int, blue: Int, alpha: Int = 255) : this(
        red / 255f,
        green / 255f,
        blue / 255f,
        alpha / 255f
    )

    constructor(argb: Int) : this(argb.red, argb.green, argb.blue, argb.alpha)
    constructor(rgb: Int, alpha: Float) : this(rgb.red, rgb.green, rgb.blue, alpha)
    constructor(rgb: Int, alpha: Int) : this(rgb.red, rgb.green, rgb.blue, alpha / 255f)

    var red = red
        get() = if (sync) ColorsFeature.instance.sync.value.red else if (rainbow) Rainbow.color.red else field

    var green = green
        get() = if (sync) ColorsFeature.instance.sync.value.green else if (rainbow) Rainbow.color.green else field

    var blue = blue
        get() = if (sync) ColorsFeature.instance.sync.value.blue else if (rainbow) Rainbow.color.blue else field

    var sync = sync
        set(value) {
            if (this != ColorsFeature.instance.sync.value) field = value
        }

    val rgb: Int
        get() {
            val a = ((alpha * 255).toInt() shl 24) and 0xFF000000.toInt()
            val r = ((red * 255).toInt() shl 16) and 0x00FF0000
            val g = ((green * 255).toInt() shl 8) and 0x0000FF00
            val b = (blue * 255).toInt() and 0x000000FF

            return a or r or g or b
        }

    companion object {
        fun rainbow(alpha: Float = 1f) = Color(0f, 0f, 0f, alpha, true)
        fun sync(alpha: Float = 1f) = Color(0f, 0f, 0f, alpha, sync = true)

        fun white(alpha: Float = 1f) = Color(1f, 1f, 1f, alpha)
    }
}


val Int.red get() = ((this shr 16) and 0xFF) / 255f
val Int.green get() = ((this shr 8) and 0xFF) / 255f
val Int.blue get() = (this and 0xFF) / 255f
val Int.alpha get() = ((this shr 24) and 0xFF) / 255f