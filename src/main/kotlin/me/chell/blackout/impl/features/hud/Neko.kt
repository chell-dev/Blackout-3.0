package me.chell.blackout.impl.features.hud

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting;
import me.chell.blackout.api.util.modId
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import kotlin.random.Random

object Neko: Widget("Neko") {

    override var description = "I <3 Neko"

    private val instance = this

    override val mainSetting = object: Setting<Boolean>("Enabled", false) {
        override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {
            if(newValue) EventManager.register(instance)
            else EventManager.unregister(instance)

            state = 2
            stateTimer = 0
            afkTimer = 0
            texture = 0
            idleAnimation = false
        }
    }

    override var width = 32
    override var height = 32


    private var afkTimer = 0

    private var stateTimer = 0
    private var state = 0

    private var texture = 0

    private var idleAnimation = false
    private var random = Random.nextInt(100, 600)


    private val alert = Identifier(modId, "textures/neko/neko_alert.png")
    private val asleep = Identifier(modId, "textures/neko/neko_asleep.png")
    private val idle = Identifier(modId, "textures/neko/neko_idle.png")

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        val id: Identifier
        val textureHeight = when(state) {
            0 -> {
                //RenderSystem.setShaderTexture(0, idle)
                id = idle
                160
            }
            1 -> {
                //RenderSystem.setShaderTexture(0, asleep)
                id = asleep
                128
            }
            2 -> {
                //RenderSystem.setShaderTexture(0, alert)
                id = alert
                32
            }
            else -> {
                id = idle
                32
            }
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        context.drawTexture(id, x.value, y.value, 0f, texture * 32f, width, height, width, textureHeight)
    }

    @EventHandler
    fun onTick(event: PlayerTickEvent) {
        if(afkTimer >= 1200) state = 1
        else afkTimer++

        when(state) {
            0 -> {
                if(idleAnimation) {
                    if(stateTimer >= 4) {
                        stateTimer = 0
                        if(texture == 4) {
                            texture = 0
                            stateTimer = 0
                            idleAnimation = false
                        } else {
                            texture++
                        }
                    } else {
                        stateTimer++
                    }
                } else {
                    if(stateTimer >= random) {
                        stateTimer = 0
                        idleAnimation = true
                        random = Random.nextInt(20, 400)
                    } else stateTimer++
                }
            }

            1 -> {
                if(stateTimer >= 10) {
                    stateTimer = 0
                    if(texture == 3) texture = 0
                    else texture++
                } else stateTimer++
            }

            2 -> {
                if(stateTimer >= 20) {
                    stateTimer = 0
                    state = 0
                    texture = 0
                    idleAnimation = false
                } else stateTimer++
            }
        }
    }

    @EventHandler
    fun onInput(event: InputEvent) {
        afkTimer = 0
        if(state == 1) {
            state = 2
            texture = 0
            stateTimer = 0
            idleAnimation = false
        }
    }
}