package me.chell.blackout.api.util

import me.chell.blackout.api.feature.Feature
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import net.minecraft.client.util.InputUtil
import java.io.File

val clientFile = mc.runDirectory.absolutePath + "/$modName/Client.txt"
val defaultConfig = mc.runDirectory.absolutePath + "/$modName/Config.txt"
val defaultFriends = mc.runDirectory.absolutePath + "/$modName/Friends.txt"

fun writeClientFile() {
    val file = File(clientFile)
    file.parentFile.mkdirs()
    file.createNewFile()

    file.writeText("$defaultConfig\r\n$defaultFriends")
}

fun readClientFile(): List<String> {
    val file = File(clientFile)

    return if (file.exists()) file.readLines() else listOf(defaultConfig, defaultFriends)
}

fun writeFeatures(fileName: String) {
    val file = File(fileName)
    file.parentFile.mkdirs()
    file.createNewFile()
    println("Created" + file.absolutePath + file.name)

    val s = "\r\n"

    val sb = StringBuilder()

    for (feature in featureManager.features) {
        sb.append("Feature: ${feature.name}$s")
        when (feature.mainSetting.value) {
            is Bind.Action -> {
                sb.append("Main: ${(feature.mainSetting as Setting<Bind.Action>).value.key.translationKey}$s")
            }

            is Bind.Toggle -> {
                sb.append("Main: ${(feature.mainSetting.value as Bind.Toggle).mode.name} ${(feature.mainSetting.value as Bind.Toggle).enabled} ${(feature.mainSetting.value as Bind.Toggle).key.translationKey}$s")
            }

            else -> {
                sb.append("Main: ${feature.mainSetting.value}$s")
            }
        }
        for (setting in feature.settings) {
            when (setting.value) {
                is Bind.Action -> {
                    sb.append("${setting.name}: ${(setting as Setting<Bind.Action>).value.key.translationKey}$s")
                }

                is Bind.Toggle -> {
                    setting as Setting<Bind.Toggle>
                    sb.append("${setting.name}: ${setting.value.mode.name} ${setting.value.enabled} ${setting.value.key.translationKey}$s")
                }

                else -> {
                    sb.append("${setting.name}: ${setting.value}$s")
                }
            }
        }
    }

    file.writeText(sb.toString())
}

fun readFeatures(fileName: String) {
    val file = File(fileName)
    if (!file.exists()) return

    var feature: Feature? = null

    for (line in file.readLines()) {
        if (line.startsWith("Feature: ")) {
            feature = featureManager.getFeatureByName(line.substring("Feature: ".length))
        } else if (line.startsWith("Main: ")) {
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
fun parseValue(setting: Setting<*>, text: String) {
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

fun writeFriends(fileName: String) {
    val file = File(fileName)
    file.parentFile.mkdirs()
    file.createNewFile()

    val sb = StringBuilder()

    for (f in friends) sb.append("$f\r\n")

    file.writeText(sb.toString())
}

fun readFriends(fileName: String) {
    val file = File(fileName)
    if (!file.exists()) return

    friends.clear()
    friends.addAll(file.readLines())
}