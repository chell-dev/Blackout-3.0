package me.chell.blackout.api.util

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.api.feature.Waypoint
import me.chell.blackout.api.feature.waypoints
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.impl.features.client.ConfigFeature
import net.minecraft.client.util.InputUtil
import java.io.File
import java.lang.reflect.Type

val clientFile = mc.runDirectory.absolutePath + "/$modName/Client.txt"
val defaultConfig = mc.runDirectory.absolutePath + "/$modName/Config.txt"
val friendsFile = mc.runDirectory.absolutePath + "/$modName/Friends.txt"
val kdFile = mc.runDirectory.absolutePath + "/$modName/KD.txt"
val waypointsFile = mc.runDirectory.absolutePath + "/$modName/Waypoints.json"

fun readConfig() {
    readClientFile()
    readFeatures()
    readWaypoints()
    readFriends()
    readKD()
}

fun writeConfig() {
    writeWaypoints()
    writeClientFile()
    writeFeatures()
    writeFriends()
    writeKD()
}

private fun writeClientFile() {
    val file = File(clientFile)
    file.parentFile.mkdirs()
    file.createNewFile()

    file.writeText(ConfigFeature.mainSetting.value.absolutePath)
}

private fun readClientFile(): String {
    val file = File(clientFile)
    file.parentFile.mkdirs()
    file.createNewFile()

    val text = file.readText()

    return if(text.isNotEmpty() && File(text).exists()) text
    else defaultConfig

}

@Suppress("unchecked_cast")
fun writeFeatures() {
    val file = ConfigFeature.mainSetting.value
    file.parentFile.mkdirs()
    file.createNewFile()
    println("Created " + file.absolutePath + file.name)

    val s = "\r\n"

    val sb = StringBuilder()

    for(feature in FeatureManager.features) {
        sb.append("Feature: ${feature.name}$s")
        when (feature.mainSetting.value) {
            is Bind.Action -> {
                sb.append("Main: ${(feature.mainSetting as Setting<Bind.Action>).value.key.translationKey}$s")
            }
            is Bind.Toggle -> {
                sb.append("Main: ${(feature.mainSetting.value as Bind.Toggle).mode.name} ${(feature.mainSetting.value as Bind.Toggle).enabled} ${(feature.mainSetting.value as Bind.Toggle).key.translationKey}$s")
            }
            is Color -> {
                val setting = feature.mainSetting as Setting<Color>
                sb.append("Main: ${setting.value.rgb} ${setting.value.rainbow} ${setting.value.sync}$s")
            }
            else -> {
                sb.append("Main: ${feature.mainSetting.value}$s")
            }
        }
        for(setting in feature.settings) {
            when (setting.value) {
                is Bind.Action -> {
                    sb.append("${setting.name}: ${(setting as Setting<Bind.Action>).value.key.translationKey}$s")
                }
                is Bind.Toggle -> {
                    setting as Setting<Bind.Toggle>
                    sb.append("${setting.name}: ${setting.value.mode.name} ${setting.value.enabled} ${setting.value.key.translationKey}$s")
                }
                is Color -> {
                    setting as Setting<Color>
                    sb.append("${setting.name}: ${setting.value.rgb} ${setting.value.rainbow} ${setting.value.sync}$s")
                }
                else -> {
                    sb.append("${setting.name}: ${setting.value}$s")
                }
            }
        }
    }

    file.writeText(sb.toString())
}

fun readFeatures() {
    val file = ConfigFeature.mainSetting.value
    if(!file.exists()) return

    var feature: Feature? = null

    for(line in file.readLines()) {
        if(line.startsWith("Feature: ")) {
            feature = FeatureManager.getFeatureByName(line.substring("Feature: ".length))
        } else if(line.startsWith("Main: ")) {
            feature ?: continue
            parseValue(feature.mainSetting, line.substring("Main: ".length))
        } else {
            val settingName = line.split(": ")[0]
            val setting = feature?.getSettingByName(settingName)
            setting ?: continue
            parseValue(setting, line.substring(settingName.length + 2))
        }
    }
}

@Suppress("unchecked_cast")
private fun parseValue(setting: Setting<*>, text: String) {
    var error = false
    try {
        when (setting.value) {
            is Boolean -> (setting as Setting<Boolean>).value = text.toBoolean()
            is Int -> (setting as Setting<Int>).value = text.toInt()
            is Double -> (setting as Setting<Double>).value = text.toDouble()
            is Float -> (setting as Setting<Float>).value = text.toFloat()
            is String -> (setting as Setting<String>).value = text
            is Char -> (setting as Setting<Char>).value = text[0]
            is Bind.Action -> (setting as Setting<Bind.Action>).value.key = InputUtil.fromTranslationKey(text)
            is Bind.Toggle -> {
                val split = text.split(" ")
                setting as Setting<Bind.Toggle>
                setting.value.mode = when (split[0]) {
                    "Toggle" -> Bind.Toggle.Mode.Toggle
                    "Hold" -> Bind.Toggle.Mode.Hold
                    else -> throw IllegalArgumentException()
                }
                setting.value.enabled = split[1].toBoolean()
                setting.value.key = InputUtil.fromTranslationKey(split[2])
            }
            is Color -> {
                val split = text.split(" ")
                val color = (setting as Setting<Color>).value
                color.rgb = split[0].toInt()
                color.rainbow = split[1].toBoolean()
                color.sync = split[2].toBoolean()
            }
            is Enum<*> -> {
                setting as Setting<Enum<*>>
                var set = false
                for (constant in setting.value::class.java.enumConstants) {
                    if (constant.name == text) {
                        setting.value = constant
                        set = true
                    }
                }
                if (!set) error = true
            }
        }
    } catch (e: Exception) {
        error = true
    }
}

private fun writeFriends() {
    val file = File(friendsFile)
    file.parentFile.mkdirs()
    file.createNewFile()

    val sb = StringBuilder()

    for(f in friends) sb.append("$f\r\n")

    file.writeText(sb.toString())
}

private fun readFriends() {
    val file = File(friendsFile)
    if(!file.exists()) return

    friends.clear()
    friends.addAll(file.readLines())
}

private fun readKD() {
    val file = File(kdFile)
    file.parentFile.mkdirs()
    file.createNewFile()
    CombatTracker.servers.clear()

    for(line in file.readLines()) {
        try {
            val split = line.split(" ")
            CombatTracker.servers[split[0]] = intArrayOf(split[1].toInt(), split[2].toInt())
        } catch (ignored: Exception) {}
    }
}

private fun writeKD() {
    val file = File(kdFile)
    file.parentFile.mkdirs()
    file.createNewFile()

    val sb = StringBuilder()
    for((ip, kd) in CombatTracker.servers) {
        sb.append("$ip ${kd[0]} ${kd[1]}")
        sb.append("\r\n")
    }
}

private fun readWaypoints() {
    waypoints.clear()
    waypoints.addAll(waypointsFile.readJson<MutableList<Waypoint>>() ?: emptyList())
}

private fun writeWaypoints() {
    waypointsFile.writeJson(waypoints)
}

private val gson = GsonBuilder().setPrettyPrinting().create()

private fun <T> String.readJson(): T? {
    val file = File(this)
    file.parentFile.mkdirs()
    file.createNewFile()

    val type: Type = object: TypeToken<T>() {}.type

    return gson.fromJson(file.readText(), type)
}

private fun String.writeJson(src: Any) {
    val file = File(this)
    file.parentFile.mkdirs()
    file.createNewFile()

    file.writeText(gson.toJson(src))
}