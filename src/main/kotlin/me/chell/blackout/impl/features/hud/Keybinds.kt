package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.featureManager
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
import java.awt.Color

object Keybinds: Widget("Keybinds") {

    override var width = 50
    override var height = textRenderer.fontHeight

    private val blacklist = register(Setting("Blacklist", mutableListOf(featureManager.getFeatureByName("GUI Bind"))))

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val list = mutableMapOf<String, Bind>()

        for(f in featureManager.features.filter { !blacklist.value.contains(it) }) {
            if(f.mainSetting.value is Bind && (f.mainSetting.value as Bind).key.code != GLFW.GLFW_KEY_UNKNOWN) list[f.name] = f.mainSetting.value as Bind

            for(s in f.settings) {
                if(s.value is Bind && (s.value as Bind).key.code != GLFW.GLFW_KEY_UNKNOWN) list[s.name] = s.value as Bind
            }
        }

        height = list.size * textRenderer.fontHeight

        var textY = 0

        for(item in list) {
            val text = "${item.key} [${item.value.key.localizedText.string.uppercase()}]"

            var color = -1
            if(item.value is Bind.Toggle) {
                color = if((item.value as Bind.Toggle).enabled) Color.GREEN.rgb
                else Color.RED.rgb
            }

            val textWidth = textRenderer.getWidth(text)
            if(textWidth > width) width = textWidth

            textRenderer.drawWithShadow(matrices, text, x.value.toFloat(), y.value + textY.toFloat(), color)
            textY += textRenderer.fontHeight
        }

    }

}