package me.chell.blackout.mixin;

import me.chell.blackout.api.event.EventManager;
import me.chell.blackout.api.events.AttackEntityEvent;
import me.chell.blackout.api.events.PlayerBreakBlockEvent;
import me.chell.blackout.api.events.PlayerInteractBlockEvent;
import me.chell.blackout.impl.features.combat.Reach;
import me.chell.blackout.impl.features.player.InteractTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
    public void getReachDistance(CallbackInfoReturnable<Float> range) {
        if (Reach.INSTANCE.getMainSetting().getValue()) range.setReturnValue(Reach.INSTANCE.getRange().getValue());
    }

    @Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
    public void breakBlockPre(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        new PlayerBreakBlockEvent(pos, PlayerBreakBlockEvent.Phase.Pre);
    }

    @Inject(method = "breakBlock", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void breakBlockPost(BlockPos pos, CallbackInfoReturnable<Boolean> cir, World world, BlockState blockState, Block block, FluidState fluidState, boolean bl) {
        if(bl) EventManager.INSTANCE.post(new PlayerBreakBlockEvent(pos, PlayerBreakBlockEvent.Phase.Post));
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    public void preInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        PlayerInteractBlockEvent.Pre event = new PlayerInteractBlockEvent.Pre(hitResult, hand, false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "interactBlock", at = @At("TAIL"))
    public void postInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if(cir.getReturnValue() != ActionResult.FAIL)
            EventManager.INSTANCE.post(new PlayerInteractBlockEvent.Post(hitResult, hand));
    }

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    public void preAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        AttackEntityEvent.Pre event = new AttackEntityEvent.Pre(target, false);
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "attackEntity", at = @At("TAIL"))
    public void postAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        EventManager.INSTANCE.post(new AttackEntityEvent.Post(target));
    }

    @Inject(method = "cancelBlockBreaking", at = @At("HEAD"), cancellable = true)
    public void cancelBlockBreaking(CallbackInfo ci) {
        if(InteractTweaks.INSTANCE.stickyBreakEnabled())
            ci.cancel();
    }

    @Inject(method = "isBreakingBlock", at = @At("HEAD"), cancellable = true)
    public void isBreakingBlock(CallbackInfoReturnable<Boolean> cir) {
        if(InteractTweaks.INSTANCE.stickyBreakEnabled()) cir.setReturnValue(false);
    }

}
