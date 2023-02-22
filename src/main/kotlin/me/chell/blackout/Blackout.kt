package me.chell.blackout

import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.impl.gui.ClientGUI

class Blackout {

    companion object {
        lateinit var instance: Blackout
    }

    lateinit var eventManager: EventManager
    lateinit var featureManager: FeatureManager
    lateinit var clientGUI: ClientGUI

    fun init() {
        instance = this

        eventManager = EventManager()
        featureManager = FeatureManager()
        featureManager.init()
        clientGUI = ClientGUI()
    }
}