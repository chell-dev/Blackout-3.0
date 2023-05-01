package me.chell.blackout.mixin;

import me.chell.blackout.api.event.EventManager;
import me.chell.blackout.api.events.RenderArmEvent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer {

    @Shadow protected abstract void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V"), cancellable = true)
    public void renderArm(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ci.cancel();

        Arm arm = (hand == Hand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();

        RenderArmEvent event = new RenderArmEvent(arm == Arm.RIGHT ? RenderArmEvent.Type.RightArm : RenderArmEvent.Type.LeftArm, matrices, equipProgress, false);
        EventManager.INSTANCE.post(event);

        if(!event.getCanceled()) {
            renderArmHoldingItem(matrices, vertexConsumers, light, event.getEquipProgress(), swingProgress, arm);
            matrices.pop();
        }
    }

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(renderMode.isFirstPerson()) {
            RenderArmEvent event = new RenderArmEvent(leftHanded ? RenderArmEvent.Type.LeftItem : RenderArmEvent.Type.RightItem, matrices, -1f, false);
            EventManager.INSTANCE.post(event);
            if(event.getCanceled()) ci.cancel();
        }
    }

    @Inject(method = "applyEquipOffset", at = @At("HEAD"), cancellable = true)
    public void equipOffset(MatrixStack matrices, Arm arm, float equipProgress, CallbackInfo ci) {
        ci.cancel();

        boolean right = arm == Arm.RIGHT;

        RenderArmEvent event = new RenderArmEvent(right ? RenderArmEvent.Type.RightItemEquip : RenderArmEvent.Type.LeftItemEquip, matrices, equipProgress, false);
        EventManager.INSTANCE.post(event);

        matrices.translate((right ? 1f : -1f) * 0.56f, -0.52f + event.getEquipProgress() * -0.6f, -0.72f);
    }

}
