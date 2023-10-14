package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.Color
import me.chell.blackout.api.util.HAlign
import me.chell.blackout.api.util.textRenderer
import net.minecraft.client.gui.DrawContext

object ArrayList: Widget("ArrayList") {

    override var description = "List enabled toggleable features"

    override var width = 50
    override var height = 9

    private val whitelist = register(Setting("Whitelist", FeatureManager.features))
    private val vAlign = register(Setting("Vertical Align", VAlign.Down))
    private val hAlign = register(Setting("Horizontal Align", HAlign.Right))
    private val color = register(Setting("Color", Color.white()))

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        var textY = 0

        val list = whitelist.value.toList()
            .filter { (it.mainSetting.value is Boolean && it.mainSetting.value == true) || (it.mainSetting.value is Bind.Toggle && (it.mainSetting.value as Bind.Toggle).enabled) }
            .sortedByDescending { textRenderer.getWidth(it.name) }
        width = textRenderer.getWidth(list[0].name)
        height = list.size * textRenderer.fontHeight

        for(feature in list) {
            val textWidth = textRenderer.getWidth(feature.name)
            val textX = when(hAlign.value) {
                HAlign.Left -> (x.value + width) - textWidth
                HAlign.Right -> x.value
                HAlign.Center -> (x.value + width / 2) - (textWidth / 2)
            }
            context.drawTextWithShadow(textRenderer, feature.name, textX, if(vAlign.value == VAlign.Up) y.value + height - textRenderer.fontHeight - textY else y.value + textY, color.value.rgb)

            textY += textRenderer.fontHeight
        }
    }

    enum class VAlign {
        Up, Down
    }
}