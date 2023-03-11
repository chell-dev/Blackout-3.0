package me.chell.blackout.api.feature

import me.chell.blackout.Blackout
import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.events.RenderHudEvent
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.mc
import me.chell.blackout.impl.gui.HudEditor
import org.reflections.Reflections
import org.reflections.scanners.Scanners

class FeatureManager {

    val features = mutableListOf<Feature>()

    fun init() {
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

    fun getFeatureByName(name: String): Feature? {
        for(feature in features) {
            if(feature.name == name) return feature
        }
        return null
    }

    @EventHandler
    fun onRenderHud(event: RenderHudEvent.Post) {
        if(mc.currentScreen is HudEditor) return
        for(widget in Blackout.instance.hudEditor.widgets) {
            if(widget.mainSetting.value)
                widget.render(event.matrices, -1, -1, event.tickDelta)
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