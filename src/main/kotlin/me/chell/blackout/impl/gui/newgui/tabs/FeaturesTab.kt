package me.chell.blackout.impl.gui.newgui.tabs

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.impl.gui.newgui.Button
import me.chell.blackout.impl.gui.newgui.Tab
import me.chell.blackout.impl.gui.newgui.Tile
import me.chell.blackout.impl.gui.newgui.buttonHeight
import net.minecraft.client.util.math.MatrixStack

object FeaturesTab: Tab("Features", 0) {

    val tiles = mutableListOf<Tile>()

    init {
        val width = 110f
        createCategory(Category.Combat, width*0, 0f, 350f)
        createCategory(Category.Movement, width*1, 0f, 150f)
        createCategory(Category.Misc, width*1, 150f, 200f)
        createCategory(Category.Render, width*2, 0f, 350f)
        createCategory(Category.Hud, width*3, 0f, 350f)
        createCategory(Category.Client, width*4, 0f, 200f)
        tiles.add(Tile("Config", mutableListOf(), width*4, 200f, width, 150f))
        tiles.add(Tile("Friends", mutableListOf(), width*5, 0f, width, 175f))
        tiles.add(Tile("Waypoints", mutableListOf(), width*5, 175f, width, 175f))
        tiles.add(Tile("Scripts", mutableListOf(), width*6, 0f, width, 350f))
    }

    private fun createCategory(category: Category, x: Float, y: Float, height: Float) {
        val tile = Tile(category.name, mutableListOf(), x, y, 110f, height)

        var buttonOffset = 0f

        for(feature in FeatureManager.features) {
            if(feature.category == category) {
                tile.buttons.add(Button(feature.name, tile, buttonOffset))
                buttonOffset += buttonHeight
            }
        }

        tiles.add(tile)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        for(tile in tiles) {
            tile.render(matrices, mouseX, mouseY, delta)
        }
    }

}