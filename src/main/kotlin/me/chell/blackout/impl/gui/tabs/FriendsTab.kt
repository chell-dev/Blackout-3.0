package me.chell.blackout.impl.gui.tabs

import me.chell.blackout.api.util.friends
import me.chell.blackout.api.util.modId
import me.chell.blackout.impl.gui.ClientGUI
import me.chell.blackout.impl.gui.GuiItem
import me.chell.blackout.impl.gui.Tab
import me.chell.blackout.impl.gui.items.AddFriendItem
import me.chell.blackout.impl.gui.items.FriendItem
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class FriendsTab(x: Int, y: Int, parent: ClientGUI): Tab(x, y, parent, Identifier(modId, "textures/gui/categories/player.png")) {

    val removeQueue = mutableListOf<FriendItem>()

    private val addButton: AddFriendItem

    init {
        var bY = parent.bannerHeight+1+ GuiItem.margin

        for(f in friends) {
            val item = FriendItem(f, size +1+ GuiItem.margin, bY, this)
            items.add(item)
            bY += item.height + GuiItem.margin
        }

        addButton = AddFriendItem(size +1+ GuiItem.margin, bY)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        if(parent.currentTab == this)
            addButton.render(matrices, mouseX, mouseY, delta)
    }

    override fun onClose() {
        super.onClose()
        addButton.onClose()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(parent.currentTab == this && addButton.mouseClicked(mouseX, mouseY, button)){
            updateItems()
            return true
        }
        if(super.mouseClicked(mouseX, mouseY, button)) {
            for(item in removeQueue) {
                items.remove(item)
            }
            removeQueue.clear()
            updateItems()
            return true
        }
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if(parent.currentTab == this && addButton.keyPressed(keyCode, scanCode, modifiers)){
            updateItems()
            return true
        }
        return false
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        return if(parent.currentTab == this)
            addButton.charTyped(chr, modifiers)
        else false
    }

    override fun updateItems() {
        var itemY = parent.bannerHeight+1+ GuiItem.margin + scrollAmount

        val knownFriends = mutableListOf<String>()

        for(item in items) {
            item.y = itemY
            itemY += item.height + margin
            knownFriends.add((item as FriendItem).friend)
        }

        for(f in friends) {
            if(knownFriends.contains(f)) continue
            val item = FriendItem(f, size +1+ GuiItem.margin, itemY, this)
            items.add(item)
            itemY += item.height + GuiItem.margin
        }

        addButton.y = itemY
    }
}