package me.chell.blackout

import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.feature.FeatureManager

class Blackout {

    companion object {
        lateinit var instance: Blackout
    }

    lateinit var eventManager: EventManager
    lateinit var featureManager: FeatureManager

    fun init() {
        instance = this

        eventManager = EventManager()
        featureManager = FeatureManager()
        featureManager.init()
    }
}