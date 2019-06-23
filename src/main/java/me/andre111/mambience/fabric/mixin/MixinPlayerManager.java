package me.andre111.mambience.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.andre111.mambience.fabric.event.PlayerJoinCallback;
import me.andre111.mambience.fabric.event.PlayerLeaveCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

	@Inject(at = @At("RETURN"), method = "onPlayerConnect")
	public void onPlayerConnect(ClientConnection clientConnection_1, ServerPlayerEntity serverPlayerEntity_1, CallbackInfo callbackInfo) {
		PlayerJoinCallback.EVENT.invoker().onPlayerJoin(clientConnection_1, serverPlayerEntity_1);
	}
	
	@Inject(at = @At("RETURN"), method = "remove")
	public void remove(ServerPlayerEntity serverPlayerEntity_1, CallbackInfo callbackInfo) {
		PlayerLeaveCallback.EVENT.invoker().onPlayerLeave(serverPlayerEntity_1);
	}
}
