package me.chell.blackout.impl.gui.items

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.friends
import me.chell.blackout.api.util.mc
import me.chell.blackout.impl.gui.GuiItem
import me.chell.blackout.impl.gui.Tab
import me.chell.blackout.impl.gui.buttons.RunnableButton
import me.chell.blackout.impl.gui.tabs.FriendsTab
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.util.math.MatrixStack

class FriendItem(val friend: String, override var x: Int, override var y: Int, val parent: FriendsTab): GuiItem() {

    override val width = 300 - Tab.size - 1 - margin - margin
    override val height = 28

    private val id = AbstractClientPlayerEntity.getSkinId(friend)

    init {
        AbstractClientPlayerEntity.loadSkin(id, friend)
    }

    override val button = RunnableButton(this, Setting("Remove", Runnable {}), false, "-")

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val center = y + (height / 2) - (mc.textRenderer.fontHeight / 2)

        RenderSystem.setShaderTexture(0, id)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        drawTexture(matrices, x + margin, y + margin, 18f, 18f, 18, 18, 144, 144)

        mc.textRenderer.drawWithShadow(matrices, friend, x + margin + 18f + margin, center.toFloat(), -1)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(mouseX >= x + width - this.button.width - margin - margin && mouseX <= x + width && mouseY >= y && mouseY <= y + height)
            friends.remove(friend); parent.removeQueue.add(this)
        return this.button.mouseClicked(mouseX, mouseY, button)
    }
}