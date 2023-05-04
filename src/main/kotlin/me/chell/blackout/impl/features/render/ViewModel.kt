package me.chell.blackout.impl.features.render

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.RenderArmEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis

object ViewModel: ToggleFeature("ViewModel", Category.Render) {

    private val page = register(Setting("Settings", Page.RightItem))

    private val fov = register(Setting("FOV Mode", FovMode.Multiplier, level = 2) { page.value == Page.All })
    private val fovMultiplier = register(Setting("FOV Multiplier", 1.0, 0.1, 2.0, level = 2) { fov.value == FovMode.Multiplier && page.value == Page.All })
    private val fovValue = register(Setting("FOV Value", 70, 30, 180, level = 2) { fov.value == FovMode.Static && page.value == Page.All })

    private val liScaleX = register(Setting("Left X Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.LeftItem })
    private val liScaleY = register(Setting("Left Y Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.LeftItem })
    private val liScaleZ = register(Setting("Left Z Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.LeftItem })
    private val liX = register(Setting("Left X Position", 0.0, -10.0, 10.0) { page.value == Page.LeftItem })
    private val liY = register(Setting("Left Y Position", 0.0, -10.0, 10.0) { page.value == Page.LeftItem })
    private val liZ = register(Setting("Left Z Position", 0.0, -10.0, 10.0) { page.value == Page.LeftItem })
    private val liRotX = register(Setting("Left X Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.LeftItem })
    private val liRotY = register(Setting("Left Y Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.LeftItem })
    private val liRotZ = register(Setting("Left Z Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.LeftItem })
    private val liEquip = register(Setting("Left Equip Progress", false) { page.value == Page.LeftItem })

    private val riScaleX = register(Setting("Right X Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.RightItem })
    private val riScaleY = register(Setting("Right Y Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.RightItem })
    private val riScaleZ = register(Setting("Right Z Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.RightItem })
    private val riX = register(Setting("Right X Position", 0.0, -10.0, 10.0) { page.value == Page.RightItem })
    private val riY = register(Setting("Right Y Position", 0.0, -10.0, 10.0) { page.value == Page.RightItem })
    private val riZ = register(Setting("Right Z Position", 0.0, -10.0, 10.0) { page.value == Page.RightItem })
    private val riRotX = register(Setting("Right X Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.RightItem })
    private val riRotY = register(Setting("Right Y Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.RightItem })
    private val riRotZ = register(Setting("Right Z Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.RightItem })
    private val riEquip = register(Setting("Right Equip Progress", false) { page.value == Page.RightItem })

    private val handScaleX = register(Setting("Hand X Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.Hand })
    private val handScaleY = register(Setting("Hand Y Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.Hand })
    private val handScaleZ = register(Setting("Hand Z Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.Hand })
    private val handX = register(Setting("Hand X Position", 1.0, -10.0, 10.0) { page.value == Page.Hand })
    private val handY = register(Setting("Hand Y Position", 1.0, -10.0, 10.0) { page.value == Page.Hand })
    private val handZ = register(Setting("Hand Z Position", 1.0, -10.0, 10.0) { page.value == Page.Hand })
    private val handRotX = register(Setting("Hand X Rotation", 1.0f, -180f, 180f, level = 2) { page.value == Page.Hand })
    private val handRotY = register(Setting("Hand Y Rotation", 1.0f, -180f, 180f, level = 2) { page.value == Page.Hand })
    private val handRotZ = register(Setting("Hand Z Rotation", 1.0f, -180f, 180f, level = 2) { page.value == Page.Hand })

    enum class Page {
        LeftItem, RightItem, Hand, All
    }

    enum class FovMode {
        Static, Multiplier
    }

    @EventHandler
    fun onRenderArm(event: RenderArmEvent) {
        when(event.type) {
            RenderArmEvent.Type.LeftArm ->{
                if(liEquip.value) event.equipProgress = 0.0f
                doTransforms(event.matrices, handScaleX.value, handScaleY.value, handScaleZ.value, handX.value, handY.value, handZ.value, handRotX.value, handRotY.value, handRotZ.value)
            }
            RenderArmEvent.Type.RightArm -> {
                if(riEquip.value) event.equipProgress = 0.0f
                doTransforms(event.matrices, handScaleX.value, handScaleY.value, handScaleZ.value, handX.value, handY.value, handZ.value, handRotX.value, handRotY.value, handRotZ.value)
            }
            RenderArmEvent.Type.LeftItem -> doTransforms(event.matrices, liScaleX.value, liScaleY.value, liScaleZ.value, liX.value, liY.value, liZ.value, liRotX.value, liRotY.value, liRotZ.value)
            RenderArmEvent.Type.RightItem -> doTransforms(event.matrices, riScaleX.value, riScaleY.value, riScaleZ.value, riX.value, riY.value, riZ.value, riRotX.value, riRotY.value, riRotZ.value)
            RenderArmEvent.Type.LeftItemEquip -> if(liEquip.value) event.equipProgress = 0.0f
            RenderArmEvent.Type.RightItemEquip -> if(riEquip.value) event.equipProgress = 0.0f
            RenderArmEvent.Type.Fov -> if(fov.value == FovMode.Static) event.fov = fovValue.value.toDouble() else event.fov *= fovMultiplier.value
        }
    }

    private fun doTransforms(matrices: MatrixStack, xS: Float, yS: Float, zS: Float, x: Double, y: Double, z: Double, xR: Float, yR: Float, zR: Float) {
        matrices.scale(xS, yS, zS)
        matrices.translate(x, y, z)
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(xR))
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yR))
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(zR))
    }

}