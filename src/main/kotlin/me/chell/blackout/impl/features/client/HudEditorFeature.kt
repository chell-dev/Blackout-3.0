package me.chell.blackout.impl.features.client

import me.chell.blackout.Blackout
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc

object HudEditorFeature: Feature("Edit HUD", Category.Hud) {
    override val mainSetting = Setting("Run", Runnable { mc.setScreen(Blackout.instance.hudEditor) })
}