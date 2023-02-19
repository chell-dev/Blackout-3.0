package me.chell.blackout.impl.gui

import net.minecraft.client.gui.DrawableHelper

abstract class GuiItem: DrawableHelper() {

    companion object {
        const val height = 30
        const val margin = 5
    }

    abstract val width: Int
    abstract var x: Int
    abstract var y: Int

}