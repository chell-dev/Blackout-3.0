package me.chell.blackout

import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.api.util.*
import me.chell.blackout.impl.gui.ClientGUI
import me.chell.blackout.impl.gui.HudEditor

class Blackout {

    companion object {
        lateinit var instance: Blackout
    }

    lateinit var eventManager: EventManager
    lateinit var featureManager: FeatureManager
    lateinit var clientGUI: ClientGUI
    lateinit var hudEditor: HudEditor

    fun init() {
        instance = this

        eventManager = EventManager()
        featureManager = FeatureManager()
        featureManager.init()

        readFeatures(readClientFile())

        clientGUI = ClientGUI()
        hudEditor = HudEditor()

        Runtime.getRuntime().addShutdownHook(Thread{
            println("Saving config")
            writeClientFile()
            writeFeatures(readClientFile())
        })
    }
}