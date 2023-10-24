package me.chell.blackout.impl.features.client

import com.mojang.serialization.Codec
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.util.mc
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.SimpleOption
import net.minecraft.client.option.SimpleOption.ValidatingIntSliderCallbacks
import net.minecraft.text.Text

object FovSlider: ToggleFeature("Unlock FOV Slider", Category.Client) {

    override fun onEnable() {}
    override fun onDisable() {}

    val setting = SimpleOption("options.fov", SimpleOption.emptyTooltip(),
        { optionText: Text, value: Int -> GameOptions.getGenericValueText(optionText, value) },
        ValidatingIntSliderCallbacks(2, 179),
        Codec.DOUBLE.xmap({ value: Double -> (value * 40.0 + 70.0).toInt() }) { value: Int -> (value.toDouble() - 70.0) / 40.0 }, 110)
    { mc.worldRenderer.scheduleTerrainUpdate() }

}