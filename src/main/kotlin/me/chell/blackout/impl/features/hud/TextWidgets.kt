package me.chell.blackout.impl.features.hud

import me.chell.blackout.api.feature.TextWidget
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.modName
import me.chell.blackout.api.util.modVersion
import me.chell.blackout.api.util.player

object Watermark: TextWidget("Watermark", { "$modName $modVersion" })

object Fps: TextWidget("FPS", { mc.currentFps.toString() + " FPS" })

object Ping: TextWidget("Ping", { player.networkHandler.getPlayerListEntry(player.uuid)!!.latency.toString() + "ms" })

object Welcome: TextWidget("Welcome", { "Welcome, ${mc.session.username}!" })

object Cps: TextWidget("Crystals Per Second", { "${Cps.value} CPS" }) {
    var value = 0
}