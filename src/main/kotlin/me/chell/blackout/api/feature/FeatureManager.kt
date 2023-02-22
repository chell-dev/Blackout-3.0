package me.chell.blackout.api.feature

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.util.eventManager
import org.reflections.Reflections
import org.reflections.scanners.Scanners

class FeatureManager {

    val features = mutableListOf<Feature>()

    init {
        registerFeatures()
        eventManager.register(this)
    }

    private fun registerFeatures() {
        val list = Reflections("me.chell.blackout.impl.features").get(Scanners.SubTypes.of(Feature::class.java).asClass<Feature>())

        for(c in list) {
            if(!c.isAnnotationPresent(NoRegister::class.java))
                features.add(c.getDeclaredConstructor().newInstance() as Feature)
        }
    }

    @EventHandler
    fun onInput(event: InputEvent) {
        for(f in features) {

            if(f.mainSetting.value is Bind) {
                (f.mainSetting.value as Bind).onKey(event)
            }

            for(s in f.settings) {
                if(s.value is Bind) {
                    (s.value as Bind).onKey(event)
                }
            }

        }
    }

}