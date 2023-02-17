package me.chell.blackout.mixin

import me.chell.blackout.api.events.PlayerTickEvent
import me.chell.blackout.api.util.eventManager
import net.minecraft.client.network.ClientPlayerEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ClientPlayerEntity::class)
class ClientPlayerEntityMixin {

    @Inject(method = ["tick"], at = [At("TAIL")])
    fun postTick(ci: CallbackInfo) {
        eventManager.post(PlayerTickEvent())
    }

}