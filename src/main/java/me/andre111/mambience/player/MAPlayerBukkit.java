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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.scan.BlockScannerBukkit;
import me.andre111.mambience.script.VariablesBukkit;

public class MAPlayerBukkit extends MAPlayer {
	public MAPlayerBukkit(Player p, MALogger logger) {
		super(p.getUniqueId(), new BlockScannerBukkit(p, EngineConfig.SIZEX, EngineConfig.SIZEY, EngineConfig.SIZEZ), new VariablesBukkit(), logger);
	}

	@Override
	public void playSound(String sound, float volume, float pitch) {
		Player player = Bukkit.getPlayer(playerUUID);
		Location location = player.getLocation();
		
		player.playSound(location, sound, SoundCategory.AMBIENT, volume, pitch);
	}

	@Override
	public void stopSound(String sound) {
		Player player = Bukkit.getPlayer(playerUUID);
		
		player.stopSound(sound);
	}
}
