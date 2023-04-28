package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW

object Keybinds: Widget("Keybinds") {

    override var width = 50
    override var height = textRenderer.fontHeight

    private val blacklist = register(Setting("Blacklist", mutableListOf(FeatureManager.getFeatureByName("GUI Bind"))))
    private val enabled = register(Setting("Enabled Color", me.chell.blackout.api.util.Color(0f, 1f, 0f)))
    private val disabled = register(Setting("Disabled Color", me.chell.blackout.api.util.Color(1f, 0f, 0f)))
    private val action = register(Setting("Action Color", me.chell.blackout.api.util.Color.white()))

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val list = mutableMapOf<String, Bind>()

        for(f in FeatureManager.features.filter { !blacklist.value.contains(it) }) {
            if(f.mainSetting.value is Bind && (f.mainSetting.value as Bind).key.code != GLFW.GLFW_KEY_UNKNOWN) list[f.name] = f.mainSetting.value as Bind

            for(s in f.settings) {
                if(s.value is Bind && (s.value as Bind).key.code != GLFW.GLFW_KEY_UNKNOWN) list[s.name] = s.value as Bind
            }
        }

        height = list.size * textRenderer.fontHeight

        var textY = 0

        for(item in list) {
            val text = "${item.key} [${item.value.key.localizedText.string.uppercase()}]"

            val color = if(item.value is Bind.Toggle) {
                if((item.value as Bind.Toggle).enabled) enabled.value
                else disabled.value
            } else action.value

            val textWidth = textRenderer.getWidth(text)
            if(textWidth > width) width = textWidth

            textRenderer.drawWithShadow(matrices, text, x.value.toFloat(), y.value + textY.toFloat(), color.rgb)
            textY += textRenderer.fontHeight
        }

    }

}