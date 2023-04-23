package me.chell.blackout.impl.gui.tabs

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.util.featureManager
import me.chell.blackout.impl.gui.ClientGUI
import me.chell.blackout.impl.gui.GuiItem
import me.chell.blackout.impl.gui.Tab
import me.chell.blackout.impl.gui.items.FeatureItem

class CategoryTab(val category: Category, x: Int, y: Int, parent: ClientGUI) : Tab(x, y, parent, category.icon) {

    init {
        var bY = parent.bannerHeight + 1 + GuiItem.margin

        for (feature in featureManager.features) {
            if (feature.category == category) {
                val item = FeatureItem(feature, size + 1 + GuiItem.margin, bY, this)
                items.add(item)
                bY += item.height + GuiItem.margin
            }
        }
    }

    @Suppress("unchecked_cast")
    override fun updateItems() {
        var itemY = parent.bannerHeight + 1 + GuiItem.margin + scrollAmount

        for (item in items as MutableList<FeatureItem>) {
            item.y = itemY
            itemY += item.fullHeight + margin
            item.updateItems()
        }
    }

}