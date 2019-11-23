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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class AccessorFabricClient extends AccessorFabric {
	public AccessorFabricClient(UUID playerUUID) {
		super(playerUUID);
	}

	// Player related methods
	@Override
	public boolean updatePlayerInstance() {
		player = MinecraftClient.getInstance().player;
		return player != null;
	}

	@Override
	public void playSound(String sound, float volume, float pitch) {
		MinecraftClient.getInstance().getSoundManager().play(new PositionedSoundInstance(new Identifier(sound), SoundCategory.AMBIENT, volume, pitch, false, 0, SoundInstance.AttenuationType.LINEAR, (float)player.getPosVector().x, (float)player.getPosVector().y, (float)player.getPosVector().z, false));
	}

	@Override
	public void stopSound(String sound) {
		MinecraftClient.getInstance().getSoundManager().stopSounds(new Identifier(sound), SoundCategory.AMBIENT);
	}
}
