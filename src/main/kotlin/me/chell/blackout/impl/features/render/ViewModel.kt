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

    private val liScale = register(Setting("Left Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.LeftItem })
    private val liX = register(Setting("Left X Position", 0.0, -10.0, 10.0) { page.value == Page.LeftItem })
    private val liY = register(Setting("Left Y Position", 0.0, -10.0, 10.0) { page.value == Page.LeftItem })
    private val liZ = register(Setting("Left Z Position", 0.0, -10.0, 10.0) { page.value == Page.LeftItem })
    private val liRotX = register(Setting("Left X Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.LeftItem })
    private val liRotY = register(Setting("Left Y Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.LeftItem })
    private val liRotZ = register(Setting("Left Z Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.LeftItem })
    private val liEquip = register(Setting("Left Equip Progress", false) { page.value == Page.LeftItem })

    private val riScale = register(Setting("Right Scale", 1.0f, 0.1f, 2f, level = 2) { page.value == Page.RightItem })
    private val riX = register(Setting("Right X Position", 0.0, -10.0, 10.0) { page.value == Page.RightItem })
    private val riY = register(Setting("Right Y Position", 0.0, -10.0, 10.0) { page.value == Page.RightItem })
    private val riZ = register(Setting("Right Z Position", 0.0, -10.0, 10.0) { page.value == Page.RightItem })
    private val riRotX = register(Setting("Right X Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.RightItem })
    private val riRotY = register(Setting("Right Y Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.RightItem })
    private val riRotZ = register(Setting("Right Z Rotation", 0.0f, -180f, 180f, level = 2) { page.value == Page.RightItem })
    private val riEquip = register(Setting("Right Equip Progress", false) { page.value == Page.RightItem })

    private val handX = register(Setting("Hand X Position", 1.0, -10.0, 10.0, level = 2) { page.value == Page.Hand })
    private val handY = register(Setting("Hand Y Position", 1.0, -10.0, 10.0, level = 2) { page.value == Page.Hand })
    private val handZ = register(Setting("Hand Z Position", 1.0, -10.0, 10.0, level = 2) { page.value == Page.Hand })
    private val handRotX = register(Setting("Hand X Rotation", 1.0f, -180f, 180f) { page.value == Page.Hand })
    private val handRotY = register(Setting("Hand Y Rotation", 1.0f, -180f, 180f) { page.value == Page.Hand })
    private val handRotZ = register(Setting("Hand Z Rotation", 1.0f, -180f, 180f) { page.value == Page.Hand })

    enum class Page {
        LeftItem, RightItem, Hand
    }

    @EventHandler
    fun onRenderArm(event: RenderArmEvent) {
        when(event.type) {
            RenderArmEvent.Type.LeftArm ->{
                if(liEquip.value) event.equipProgress = 0.0f
                doTransforms(event.matrices, 1.0f, handX.value, handY.value, handZ.value, handRotX.value, handRotY.value, handRotZ.value)
            }
            RenderArmEvent.Type.RightArm -> {
                if(riEquip.value) event.equipProgress = 0.0f
                doTransforms(event.matrices, 1.0f, handX.value, handY.value, handZ.value, handRotX.value, handRotY.value, handRotZ.value)
            }
            RenderArmEvent.Type.LeftItem -> doTransforms(event.matrices, liScale.value, liX.value, liY.value, liZ.value, liRotX.value, liRotY.value, liRotZ.value)
            RenderArmEvent.Type.RightItem -> doTransforms(event.matrices, riScale.value, riX.value, riY.value, riZ.value, riRotX.value, riRotY.value, riRotZ.value)
            RenderArmEvent.Type.LeftItemEquip -> if(liEquip.value) event.equipProgress = 0.0f
            RenderArmEvent.Type.RightItemEquip -> if(riEquip.value) event.equipProgress = 0.0f
        }
    }

    private fun doTransforms(matrices: MatrixStack, scale: Float, x: Double, y: Double, z: Double, xR: Float, yR: Float, zR: Float) {
        matrices.scale(scale, scale, scale)
        matrices.translate(x, y, z)
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(xR))
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yR))
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(zR))
    }

}