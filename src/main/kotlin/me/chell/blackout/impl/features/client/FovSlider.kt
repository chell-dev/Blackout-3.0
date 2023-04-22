package me.chell.blackout.impl.features.client

import com.mojang.serialization.Codec
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.SimpleOption
import net.minecraft.client.option.SimpleOption.ValidatingIntSliderCallbacks
import net.minecraft.text.Text

class FovSlider: ToggleFeature("Custom FOV", Category.Client, false) {

    private val slider = register(object : Setting<Int>("Value", 90, 2, 179) {
        override fun onValueChanged(oldValue: Int, newValue: Int) {
            setting.value = newValue
        }
    })

    override fun onEnable() {
        setting.value = slider.value
    }

    override fun onDisable() {}

    private val setting = SimpleOption("options.fov", SimpleOption.emptyTooltip(),
        { optionText: Text, value: Int -> GameOptions.getGenericValueText(optionText, value) },
        ValidatingIntSliderCallbacks(2, 179),
        Codec.DOUBLE.xmap({ value: Double -> (value * 40.0 + 70.0).toInt() }) { value: Int -> (value.toDouble() - 70.0) / 40.0 }, 110)
    { mc.worldRenderer.scheduleTerrainUpdate() }

    companion object {
        lateinit var fov: SimpleOption<Int>
        lateinit var enabled: Setting<Boolean>
    }

    init {
        fov = setting
        enabled = mainSetting
    }

}