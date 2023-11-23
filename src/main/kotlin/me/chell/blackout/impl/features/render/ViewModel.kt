package me.chell.blackout.impl.features.render

import me.chell.blackout.api.event.EventHandler
import me.chell.blackout.api.events.RenderArmEvent
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.ToggleFeature
import me.chell.blackout.api.setting.Setting
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis

object ViewModel: ToggleFeature("ViewModel", Category.Render) {

    private val page = Setting("Settings", Page.RightItem)

    private val fov = Setting("FOV Mode", FovMode.Multiplier, level = 2) { page.value == Page.All }
    private val fovMultiplier = Setting("FOV Multiplier", 1.0, 0.1, 2.0, level = 2) { fov.value == FovMode.Multiplier && page.value == Page.All }
    private val fovValue = Setting("FOV Value", 70, 30, 180, level = 2) { fov.value == FovMode.Static && page.value == Page.All }

    private val liScaleX = Setting("Left X Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.LeftItem }
    private val liScaleY = Setting("Left Y Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.LeftItem }
    private val liScaleZ = Setting("Left Z Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.LeftItem }
    private val liX = Setting("Left X Position", 0.0, -10.0, 10.0) { page.value == Page.LeftItem }
    private val liY = Setting("Left Y Position", 0.0, -10.0, 10.0) { page.value == Page.LeftItem }
    private val liZ = Setting("Left Z Position", 0.0, -10.0, 10.0) { page.value == Page.LeftItem }
    private val liRotX = Setting("Left X Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.LeftItem }
    private val liRotY = Setting("Left Y Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.LeftItem }
    private val liRotZ = Setting("Left Z Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.LeftItem }
    private val liEquip = Setting("Left Equip Progress", false) { page.value == Page.LeftItem }

    private val riScaleX = Setting("Right X Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.RightItem }
    private val riScaleY = Setting("Right Y Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.RightItem }
    private val riScaleZ = Setting("Right Z Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.RightItem }
    private val riX = Setting("Right X Position", 0.0, -10.0, 10.0) { page.value == Page.RightItem }
    private val riY = Setting("Right Y Position", 0.0, -10.0, 10.0) { page.value == Page.RightItem }
    private val riZ = Setting("Right Z Position", 0.0, -10.0, 10.0) { page.value == Page.RightItem }
    private val riRotX = Setting("Right X Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.RightItem }
    private val riRotY = Setting("Right Y Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.RightItem }
    private val riRotZ = Setting("Right Z Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.RightItem }
    private val riEquip = Setting("Right Equip Progress", false) { page.value == Page.RightItem }

    private val handScaleX = Setting("Hand X Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.Hand }
    private val handScaleY = Setting("Hand Y Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.Hand }
    private val handScaleZ = Setting("Hand Z Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.Hand }
    private val handX = Setting("Hand X Position", 1.0, -10.0, 10.0) { page.value == Page.Hand }
    private val handY = Setting("Hand Y Position", 1.0, -10.0, 10.0) { page.value == Page.Hand }
    private val handZ = Setting("Hand Z Position", 1.0, -10.0, 10.0) { page.value == Page.Hand }
    private val handRotX = Setting("Hand X Rotation", 1.0f, -180f, 180f, level = 2) { page.value == Page.Hand }
    private val handRotY = Setting("Hand Y Rotation", 1.0f, -180f, 180f, level = 2) { page.value == Page.Hand }
    private val handRotZ = Setting("Hand Z Rotation", 1.0f, -180f, 180f, level = 2) { page.value == Page.Hand }

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