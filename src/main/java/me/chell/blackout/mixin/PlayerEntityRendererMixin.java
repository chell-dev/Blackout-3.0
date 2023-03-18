package me.chell.blackout.mixin;

import me.chell.blackout.api.events.RenderNametagEvent;
import me.chell.blackout.api.util.GlobalsKt;
import me.chell.blackout.impl.features.render.FirstPersonBody;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.datafixer.fix.ChunkPalettedStorageFix;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "setModelPose", at = @At("TAIL"))
    public void setModelPose(AbstractClientPlayerEntity player, CallbackInfo ci) {
        if(FirstPersonBody.Companion.isActive() && player == GlobalsKt.getPlayer()) {
            getModel().head.visible = false;
            getModel().hat.visible = false;
        }
    }

    @Inject(method = "getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    public void getPositionOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, CallbackInfoReturnable<Vec3d> cir) {
        if(!FirstPersonBody.Companion.isActive()) return;

        float z = MathHelper.cos(-GlobalsKt.getPlayer().getYaw() * ((float)Math.PI / 180) - (float)Math.PI);
        float x = MathHelper.sin(-GlobalsKt.getPlayer().getYaw() * ((float)Math.PI / 180) - (float)Math.PI);
        cir.setReturnValue(new Vec3d(x * 0.3, abstractClientPlayerEntity.isInSneakingPose() ? -0.125 : 0.0, z * 0.3));
    }

}
