package me.chell.blackout.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.ParseResults;
import me.chell.blackout.api.event.EventManager;
import me.chell.blackout.api.events.ChatSendEvent;
import me.chell.blackout.api.events.MessageReceivedEvent;
import me.chell.blackout.api.events.PlayerKnockbackEvent;
import me.chell.blackout.api.util.GlobalsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {

    @Shadow private LastSeenMessagesCollector lastSeenMessagesCollector;

    @Shadow private MessageChain.Packer messagePacker;

    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Shadow protected abstract ParseResults<CommandSource> parse(String command);

    @Shadow private MessageSignatureStorage signatureStorage;

    @Inject(method = "onExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"), cancellable = true)
    public void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
        PlayerKnockbackEvent event = new PlayerKnockbackEvent();
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocityClient(DDD)V"), cancellable = true)
    public void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        if(packet.getId() != GlobalsKt.getPlayer().getId()) return;

        PlayerKnockbackEvent event = new PlayerKnockbackEvent();
        EventManager.INSTANCE.post(event);
        if(event.getCanceled()) ci.cancel();
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String content, CallbackInfo ci) {
        ChatSendEvent event = new ChatSendEvent(content, false, false);
        EventManager.INSTANCE.post(event);

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
        EventManager.INSTANCE.post(event);

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

    @Inject(method = "onChatMessage", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/network/message/MessageHandler;onChatMessage(Lnet/minecraft/network/message/SignedMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V"),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci, Optional<MessageBody> optional, Optional<MessageType.Parameters> optional2, UUID uUID, PlayerListEntry playerListEntry, PublicPlayerSession publicPlayerSession, MessageLink messageLink, SignedMessage signedMessage) {
        MessageReceivedEvent.Chat event = new MessageReceivedEvent.Chat(signedMessage, playerListEntry.getProfile(), optional2.get(), MessageReceivedEvent.Phase.Pre);
        EventManager.INSTANCE.post(event);

        SignedMessage eventMessage = event.getMessage();
        GameProfile eventSender = event.getSender();
        MessageType.Parameters eventParams = event.getParams();

        ci.cancel();

        client.getMessageHandler().onChatMessage(eventMessage, eventSender, eventParams);
        signatureStorage.add(event.getMessage());

        EventManager.INSTANCE.post(new MessageReceivedEvent.Chat(eventMessage, eventSender, eventParams, MessageReceivedEvent.Phase.Post));
    }

    @Inject(method = "onProfilelessChatMessage", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/network/message/MessageHandler;onProfilelessMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageType$Parameters;)V"),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet, CallbackInfo ci, Optional<MessageType.Parameters> optional) {
        MessageReceivedEvent.Profileless event = new MessageReceivedEvent.Profileless(packet.message(), optional.get(), MessageReceivedEvent.Phase.Pre);
        EventManager.INSTANCE.post(event);

        Text eventContent = event.getContent();
        MessageType.Parameters eventParams = event.getParams();

        ci.cancel();

        client.getMessageHandler().onProfilelessMessage(eventContent, eventParams);

        EventManager.INSTANCE.post(new MessageReceivedEvent.Profileless(eventContent, eventParams, MessageReceivedEvent.Phase.Post));
    }

    @Inject(method = "onGameMessage", at = @At(value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/network/message/MessageHandler;onGameMessage(Lnet/minecraft/text/Text;Z)V"),
            cancellable = true
    )
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        MessageReceivedEvent.Game event = new MessageReceivedEvent.Game(packet.content(), packet.overlay(), MessageReceivedEvent.Phase.Pre);
        EventManager.INSTANCE.post(event);

        Text eventMessage = event.getMessage();
        boolean eventOverlay = event.getOverlay();

        ci.cancel();

        client.getMessageHandler().onGameMessage(eventMessage, eventOverlay);

        EventManager.INSTANCE.post(new MessageReceivedEvent.Game(eventMessage, eventOverlay, MessageReceivedEvent.Phase.Post));
    }

}
