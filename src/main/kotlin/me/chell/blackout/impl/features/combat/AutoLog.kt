package me.chell.blackout.impl.features.combat

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.player
import net.minecraft.text.Text

object AutoLog: ToggleFeature("AutoLog", Category.Combat) {

    override var description = "Disconnect when your health goes below the threshold"

    private val health = register(Setting("Health", 13.0f, 0.0f, 36.0f))

    @EventHandler
    fun onPlayerTick(event: PlayerTickEvent) {
        if ((player.health + player.absorptionAmount) <= health.value) {
            //world.disconnect()
            val connection = player.networkHandler.connection
            if (connection.isOpen && mainSetting.value) {
                connection.disconnect(Text.of("[AutoLog] Health was below ${health.value}!\n\nAutolog disabled."))
                mainSetting.value = false
            }
        }
    }
}