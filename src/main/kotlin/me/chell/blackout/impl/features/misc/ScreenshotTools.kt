package me.chell.blackout.impl.features.misc

import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.mc
import me.chell.blackout.api.util.sendClientMessage
import net.minecraft.client.util.ScreenshotRecorder
import net.minecraft.util.Util
import java.io.File

object ScreenshotTools: Feature("Screenshot Tools", Category.Misc) {

    override val mainSetting = Setting("Empty", null)

    private val screenshotsFolder = File(mc.runDirectory, ScreenshotRecorder.SCREENSHOTS_DIRECTORY)

    private val panoramaButton = Setting("Take Panorama", Runnable { sendClientMessage(mc.takePanorama(getPanoramaFolder(), 1024, 1024)) })
    private val folderButton = Setting("Open Screenshots Folder", Runnable { Util.getOperatingSystem().open(screenshotsFolder) })

    //net.minecraft.client.util.ScreenshotRecorder.getScreenshotFilename
    private fun getPanoramaFolder(): File {
        val directory = screenshotsFolder
        val string = "Panorama_${Util.getFormattedCurrentTime()}"
        var i = 1
        var file: File
        while (File(directory, string + (if (i == 1) "" else "_$i")).also { file = it }.exists()) {
            ++i
        }
        File(file, ScreenshotRecorder.SCREENSHOTS_DIRECTORY).mkdirs()
        return file
    }
}