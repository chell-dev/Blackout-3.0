package me.chell.blackout.api.util

import net.fabricmc.loader.impl.FabricLoaderImpl
import net.minecraft.util.JsonHelper
import java.net.URL
import java.nio.file.Files
import java.util.zip.ZipFile
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
        val files = FabricLoaderImpl.INSTANCE.modsDirectory.listFiles()!!.filter { it.extension.equals("jar", true) }

        for(file in files) {
            val zip = ZipFile(file)
            val entry = zip.getEntry("fabric.mod.json") ?: continue
            val json = JsonHelper.deserialize(zip.getInputStream(entry).bufferedReader())

            if(json.get("id").asString == modId) {
                file.delete()
                break
            }
        }
    }

    private fun downloadLatest() {
        val json = JsonHelper.deserialize(URL("https://api.github.com/repos/${repo}/releases/latest").readText())

        val tag = json.get("tag_name").asString
        val download = json.get("assets").asJsonArray[0].asJsonObject.get("browser_download_url").asString

        URL(download).openStream().use { Files.copy(it, Path(FabricLoaderImpl.INSTANCE.modsDirectory.absolutePath + "/${modId}-${tag}.jar")) }
    }

}