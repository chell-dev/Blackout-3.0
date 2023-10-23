package me.chell.blackout.impl.gui.items

import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.friends
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.textRenderer
import me.chell.blackout.impl.gui.GuiItem
import me.chell.blackout.impl.gui.Tab
import me.chell.blackout.impl.gui.buttons.RunnableButton
import me.chell.blackout.impl.gui.tabs.FriendsTab
import net.minecraft.client.gui.DrawContext

class FriendItem(val friend: String, override var x: Int, override var y: Int, val parent: FriendsTab): GuiItem(parent) {

    override val width = 300 - Tab.size - 1 - margin - margin
    override var height = 28

    //private val id = AbstractClientPlayerEntity.getSkinId(friend)

    init {
        //AbstractClientPlayerEntity.loadSkin(id, friend)
    }

    override val button = RunnableButton(this, Setting("Remove", Runnable {}), false, "-")

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val center = y + (height / 2) - (mc.textRenderer.fontHeight / 2)

        // todo fix
        //RenderSystem.setShaderTexture(0, id)
        //RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        //RenderSystem.enableBlend()
        //RenderSystem.defaultBlendFunc()
        //RenderSystem.enableDepthTest()
        //drawTexture(matrices, x + margin, y + margin, 18f, 18f, 18, 18, 144, 144)

        context.drawTextWithShadow(textRenderer, friend, x + margin + 18 + margin, center, -1)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(mouseX >= x + width - this.button.width - margin - margin && mouseX <= x + width && mouseY >= y && mouseY <= y + height)
            friends.remove(friend); parent.removeQueue.add(this)
        return this.button.mouseClicked(mouseX, mouseY, button)
    }
}