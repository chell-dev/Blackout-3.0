package me.chell.blackout.mixin;

import com.mojang.authlib.GameProfile;
import me.chell.blackout.impl.features.render.Cosmetics;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    @Shadow private @Nullable PlayerListEntry playerListEntry;

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique private Identifier id = new Identifier("blackout", "textures/cape.png");

    @Inject(method = "getSkinTextures", at = @At("HEAD"), cancellable = true)
    public void cape(CallbackInfoReturnable<SkinTextures> cir) {
        SkinTextures st = playerListEntry == null ? DefaultSkinHelper.getSkinTextures(getUuid()) : playerListEntry.getSkinTextures();
        if(Cosmetics.INSTANCE.getCape(getName().getString())) cir.setReturnValue(new SkinTextures(st.texture(), st.textureUrl(), st.capeTexture(), id, st.model(), st.secure()));
    }

}
