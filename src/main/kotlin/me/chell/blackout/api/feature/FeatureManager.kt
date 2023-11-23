package me.chell.blackout.api.feature

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.events.RenderHudEvent
import me.chell.blackout.api.events.RenderWorldEvent
import me.chell.blackout.api.events.SetScreenEvent
import me.chell.blackout.api.setting.Bind
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.drawBox
import me.chell.blackout.api.util.mc
import me.chell.blackout.impl.gui.old.HudEditor
import net.minecraft.client.util.InputUtil
import net.minecraft.util.math.Box
import org.lwjgl.glfw.GLFW
import org.reflections.Reflections
import org.reflections.scanners.Scanners

object FeatureManager {

    val features = mutableListOf<Feature>()
    val binds = mutableListOf<Bind>()

    fun init() {
        registerFeatures()
        collectBinds()
        EventManager.register(this)
    }

    private fun registerFeatures() {
        val list = Reflections("me.chell.blackout.impl.features").get(Scanners.SubTypes.of(Feature::class.java).asClass<Feature>())

        for(c in list) {
            if(c.isAnnotationPresent(NoRegister::class.java)) continue

            val feature = c.kotlin.objectInstance as Feature? ?: c.getDeclaredConstructor().newInstance() as Feature
            features.add(feature)

            for(field in c.declaredFields) {
                if(field.type == Setting::class.java) {
                    if(field.isAnnotationPresent(NoRegister::class.java) || field.name == "mainSetting") continue

                    val wasAccessible = field.isAccessible
                    field.isAccessible = true
                    feature.settings.add(field.get(feature) as Setting<*>)
                    field.isAccessible = wasAccessible
                }
            }
        }
    }

    private fun collectBinds() {
        for(f in features) {
            val main = f.mainSetting.value
            if(main is Bind) binds.add(main)

            for(s in f.settings) {
                val v = s.value
                if(v is Bind) binds.add(v)
            }

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
        for(widget in HudEditor.widgets) {
            if(widget.mainSetting.value)
                widget.render(event.context, -1, -1, event.tickDelta)
        }
    }

    @EventHandler
    fun onInput(event: InputEvent) {
        for(bind in binds) {
            bind.onKey(event)
        }
    }

    @EventHandler
    fun onOpenGui(event: SetScreenEvent) {
        if(mc.currentScreen != null) return
        for(bind in binds) {
            if(bind is Bind.Toggle && bind.mode == Bind.Toggle.Mode.Hold) {
                if(when(bind.key.category) {
                    InputUtil.Type.KEYSYM -> GLFW.glfwGetKey(mc.window.handle, bind.key.code) == 1
                    InputUtil.Type.SCANCODE -> false
                    InputUtil.Type.MOUSE -> GLFW.glfwGetMouseButton(mc.window.handle, bind.key.code) == 1
                }) bind.enabled = !bind.enabled
            }
        }
    }

    @EventHandler
    fun onRender3D(event: RenderWorldEvent) {
        for(w in waypoints) drawBox(Box.of(w.pos, 0.5, 1.0, 0.5), w.color)
    }

}