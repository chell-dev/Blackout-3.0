package me.chell.blackout.mixin;

import com.mojang.authlib.GameProfile;
import me.chell.blackout.impl.features.render.Cosmetics;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    Identifier id = new Identifier("blackout", "textures/cape.png");

    @Inject(method = "getCapeTexture", at = @At("HEAD"), cancellable = true)
    public void cape(CallbackInfoReturnable<Identifier> cir) {
        if(Cosmetics.INSTANCE.getCape(getName().getString())) cir.setReturnValue(id);
    }

}
