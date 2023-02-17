package me.chell.blackout

import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.feature.FeatureManager

class Blackout {

    /*
        modules
        hud
        fov slider
        gui
        waypoints
    */

    companion object {
        lateinit var instance: Blackout
    }

    lateinit var eventManager: EventManager

    fun init() {
        instance = this

        eventManager = EventManager()
        FeatureManager()
    }
}