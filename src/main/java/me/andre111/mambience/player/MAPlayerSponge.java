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
package me.andre111.mambience.player;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import me.andre111.mambience.MALogger;

public class MAPlayerSponge extends MAPlayer {
	public MAPlayerSponge(Player p, MALogger logger) {
		super(p.getUniqueId(), new AccessorSponge(p.getUniqueId()), logger);
	}
	
	@Override
	public void playSound(String sound, float volume, float pitch) {
		Optional<Player> optPlayer = Sponge.getServer().getPlayer(getPlayerUUID());
		if(optPlayer.isPresent()) {
			optPlayer.get().playSound(SoundType.builder().build(sound), SoundCategories.AMBIENT, optPlayer.get().getLocation().getPosition(), volume, pitch);
		}
	}

	@Override
	public void stopSound(String sound) {
		Optional<Player> optPlayer = Sponge.getServer().getPlayer(getPlayerUUID());
		if(optPlayer.isPresent()) {
			optPlayer.get().stopSounds(SoundType.builder().build(sound), SoundCategories.AMBIENT);
		}
	}
}
