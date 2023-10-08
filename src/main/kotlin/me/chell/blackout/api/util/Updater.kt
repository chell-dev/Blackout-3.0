package me.chell.blackout.api.util

import net.fabricmc.loader.impl.FabricLoaderImpl
import net.minecraft.util.JsonHelper
import java.io.File
import java.net.URL
import java.nio.file.Files
import kotlin.io.path.Path

object Updater {

    private const val repo = "chell-dev/Blackout-3.0"

    val latestVersion = JsonHelper.deserialize(URL("https://api.github.com/repos/${repo}/releases/latest").readText())
        .get("tag_name").asString

    var updateAvailable = false

    fun checkUpdates() {
        updateAvailable = modVersion != latestVersion
        println("Update available: $updateAvailable")
    }

    fun update() {
        deleteOld()
        downloadLatest()
        mc.scheduleStop()
    }

    private fun deleteOld() {
        val tags = JsonHelper.deserialize(URL("https://api.github.com/repos/${repo}/tags").readText()).asJsonArray

        val files = FabricLoaderImpl.INSTANCE.modsDirectory.listFiles()!!

        for(tagJson in tags) {
            val tag = tagJson.asJsonObject.get("name").asString
            if(files.any { it.name == "${modId}-${tag}.jar" }) File(FabricLoaderImpl.INSTANCE.modsDirectory.absolutePath + "/${modId}-${tag}.jar").delete()
        }
    }

    private fun downloadLatest() {
        val json = JsonHelper.deserialize(URL("https://api.github.com/repos/${repo}/releases/latest").readText())

        val tag = json.get("tag_name").asString
        val download = json.get("assets").asJsonArray[0].asJsonObject.get("browser_download_url").asString

        URL(download).openStream().use { Files.copy(it, Path(FabricLoaderImpl.INSTANCE.modsDirectory.absolutePath + "/${modId}-${tag}.jar")) }
    }

}