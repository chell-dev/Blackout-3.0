package me.chell.blackout.mixin;

import me.chell.blackout.impl.features.render.Cosmetics;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Deadmau5FeatureRenderer.class)
public abstract class Deadmau5FeatureRendererMixin extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public Deadmau5FeatureRendererMixin(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ci.cancel();
        if(!Cosmetics.INSTANCE.getEars(abstractClientPlayerEntity.getName().getString()) || abstractClientPlayerEntity.isInvisible()) return;

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getSkinTextures().texture()));
        int m = LivingEntityRenderer.getOverlay(abstractClientPlayerEntity, 0.0f);
        for (int n = 0; n < 2; ++n) {
            float o = MathHelper.lerp(h, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.getYaw()) - MathHelper.lerp(h, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
            float p = MathHelper.lerp(h, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.getPitch());
            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(o));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(p));
            matrixStack.translate(0.375f * (float)(n * 2 - 1), 0.0f, 0.0f);
            matrixStack.translate(0.0f, -0.375f, 0.0f);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-p));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-o));
            float q = 1.3333334f;
            matrixStack.scale(q, q, q);
            getContextModel().renderEars(matrixStack, vertexConsumer, i, m);
            matrixStack.pop();
        }
    }

}
