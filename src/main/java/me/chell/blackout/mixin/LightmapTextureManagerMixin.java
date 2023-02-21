package me.chell.blackout.mixin;

import me.chell.blackout.api.util.GlobalsKt;
import me.chell.blackout.impl.features.render.Fullbright;
import net.minecraft.client.render.LightmapTextureManager;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;lerp(Lorg/joml/Vector3fc;F)Lorg/joml/Vector3f;", ordinal = 5))
    public Vector3f lerp(Vector3f instance, Vector3fc other, float t) {
        if(Fullbright.instance.getMainSetting().getValue()) t = 1000f;
        else t = GlobalsKt.getMc().options.getGamma().getValue().floatValue();

        return instance.lerp(other, t, instance);
    }

}
