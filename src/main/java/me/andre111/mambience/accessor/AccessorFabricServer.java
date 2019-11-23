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
package me.andre111.mambience.accessor;

import java.util.UUID;

import me.andre111.mambience.MAmbienceFabric;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class AccessorFabricServer extends AccessorFabric {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	private ServerPlayerEntity serverPlayer;
	
	public AccessorFabricServer(UUID playerUUID) {
		super(playerUUID);
	}

	// Player related methods
	@Override
	public boolean updatePlayerInstance() {
		player = serverPlayer = MAmbienceFabric.server.getPlayerManager().getPlayer(playerUUID);
		return player != null;
	}
	
	@Override
	public void playSound(String sound, float volume, float pitch) {
		//SoundEvent event = new SoundEvent(new Identifier(sound));
		//player.playSound(event, SoundCategory.AMBIENT, volume, pitch); // only works for registered sound events -> only when client has mod installed
		// also only works for registered sound events -> only when client has mod installed, but will bind sounds to player (no longer positional)
		//player.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(event, SoundCategory.AMBIENT, player, volume, pitch));
		serverPlayer.networkHandler.sendPacket(new PlaySoundIdS2CPacket(new Identifier(sound), SoundCategory.AMBIENT, player.getPosVector(), volume, pitch));
	}

	@Override
	public void stopSound(String sound) {
		serverPlayer.networkHandler.sendPacket(new StopSoundS2CPacket(new Identifier(sound), SoundCategory.AMBIENT));
	}
}
