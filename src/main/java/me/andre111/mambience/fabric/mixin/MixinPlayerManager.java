/*
 * Copyright (c) 2019 André Schweiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
