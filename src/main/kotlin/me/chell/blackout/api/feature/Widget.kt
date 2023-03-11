package me.chell.blackout.api.feature

import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.impl.gui.HudEditor
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color

@NoRegister
abstract class Widget(name: String): Feature(name, Category.Hud) {

    override val mainSetting = Setting("Enabled", false)

    val x = register(Setting("X", 0, 0, 0){false})
    val y = register(Setting("Y", 0, 0, 0){false})

    abstract var width: Int
    abstract var height: Int

    private var deltaX = 0
    private var deltaY = 0
    private var grabbed = false

    private val background = Color(100, 100, 100, 150).rgb

    open fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float){
        if(grabbed) {
            x.value = mouseX - deltaX
            y.value = mouseY - deltaY
        }

        if(x.value < 0) x.value = 0
        if(y.value < 0) y.value = 0
        if(x.value + width > mc.window.scaledWidth) x.value = mc.window.scaledWidth - width
        if(y.value + height > mc.window.scaledHeight) y.value = mc.window.scaledHeight - height

        if(mc.currentScreen is HudEditor) {
            DrawableHelper.fill(matrices, x.value, y.value, x.value + width, y.value + height, background)
        }
    }

    open fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button == 0 && mouseX >= x.value && mouseX <= x.value + width && mouseY >= y.value && mouseY <= y.value + height) {
            grabbed = true
            deltaX = mouseX.toInt() - x.value
            deltaY = mouseY.toInt() - y.value
            return true
        }

        return false
    }

    open fun mouseReleased() {
        grabbed = false
    }

}