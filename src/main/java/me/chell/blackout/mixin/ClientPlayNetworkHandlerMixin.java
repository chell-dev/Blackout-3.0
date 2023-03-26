package me.chell.blackout.mixin;

import com.mojang.brigadier.ParseResults;
import me.chell.blackout.api.events.ChatSendEvent;
import me.chell.blackout.api.events.PlayerKnockbackEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.network.Packet;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow private LastSeenMessagesCollector lastSeenMessagesCollector;

    @Shadow private MessageChain.Packer messagePacker;

    @Shadow public abstract void sendPacket(Packet<?> packet);

    @Shadow protected abstract ParseResults<CommandSource> parse(String command);

    @Inject(method = "onExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"), cancellable = true)
    public void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
        PlayerKnockbackEvent event = new PlayerKnockbackEvent();
        GlobalsKt.getEventManager().post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocityClient(DDD)V"), cancellable = true)
    public void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        if(packet.getId() != GlobalsKt.getPlayer().getId()) return;

        PlayerKnockbackEvent event = new PlayerKnockbackEvent();
        GlobalsKt.getEventManager().post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String content, CallbackInfo ci) {
        ChatSendEvent event = new ChatSendEvent(content, false, false);
        GlobalsKt.getEventManager().post(event);

        ci.cancel();
        if(event.getCanceled()) return;
        content = event.getText();

        Instant instant = Instant.now();
        long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
        LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = lastSeenMessagesCollector.collect();
        MessageSignatureData messageSignatureData = messagePacker.pack(new MessageBody(content, instant, l, lastSeenMessages.lastSeen()));
        sendPacket(new ChatMessageC2SPacket(content, instant, l, messageSignatureData, lastSeenMessages.update()));
    }

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    public void sendChatCommand(String command, CallbackInfo ci) {
        ChatSendEvent event = new ChatSendEvent(command, true, false);
        GlobalsKt.getEventManager().post(event);

        ci.cancel();
        if(event.getCanceled()) return;
        command = event.getText();

        Instant instant = Instant.now();
        long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
        LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = this.lastSeenMessagesCollector.collect();
        ArgumentSignatureDataMap argumentSignatureDataMap = ArgumentSignatureDataMap.sign(SignedArgumentList.of(parse(command)), value -> {
            MessageBody messageBody = new MessageBody(value, instant, l, lastSeenMessages.lastSeen());
            return this.messagePacker.pack(messageBody);
        });
        this.sendPacket(new CommandExecutionC2SPacket(command, instant, l, argumentSignatureDataMap, lastSeenMessages.update()));
    }

}
