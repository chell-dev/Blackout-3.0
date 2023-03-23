package me.chell.blackout.impl.features.hud

import com.mojang.blaze3d.systems.RenderSystem
import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.InputEvent
import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.feature.Widget
import me.chell.blackout.api.setting.Setting
import me.chell.blackout.api.util.eventManager
import me.chell.blackout.api.util.modId
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import kotlin.random.Random

class Neko: Widget("Neko") {

    private val instance = this

    override val mainSetting = object: Setting<Boolean>("Enabled", false) {
        override fun onValueChanged(oldValue: Boolean, newValue: Boolean) {
            if(newValue) eventManager.register(instance)
            else eventManager.unregister(instance)

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

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        val textureHeight = when(state) {
            0 -> {
                RenderSystem.setShaderTexture(0, idle)
                160
            }
            1 -> {
                RenderSystem.setShaderTexture(0, asleep)
                128
            }
            2 -> {
                RenderSystem.setShaderTexture(0, alert)
                32
            }
            else -> 32
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        DrawableHelper.drawTexture(matrices, x.value, y.value, 0f, texture * 32f, width, height, width, textureHeight)
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