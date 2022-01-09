/*
 * Copyright (c) 2022 Andre Schweiger
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
package me.andre111.mambience;

import java.util.HashMap;
import java.util.UUID;

import me.andre111.mambience.accessor.Accessor;
import me.andre111.mambience.config.Config;
import me.andre111.mambience.footstep.Footsteps;
import me.andre111.mambience.scan.BlockScanner;
import me.andre111.mambience.scan.Variables;
import me.andre111.mambience.sound.SoundPlayer;

public final class MAPlayer {
	private final UUID playerUUID;
	private final Accessor accessor;
	private final BlockScanner scanner;
	private final Variables variables;
	private final Footsteps footsteps;
	private final SoundPlayer soundPlayer;
	private final MALogger logger;
	private final HashMap<String, Integer> cooldowns;
	
	public MAPlayer(UUID playerUUID, Accessor accessor, MALogger logger) {
		this.playerUUID = playerUUID;
		this.accessor = accessor;
		this.scanner = new BlockScanner(accessor, Config.scanner().getSizeX(), Config.scanner().getSizeY(), Config.scanner().getSizeZ());
		this.variables = new Variables(accessor);
		this.footsteps = new Footsteps(this);
		this.soundPlayer = new SoundPlayer(accessor, logger);
		this.logger = logger;
		this.cooldowns = new HashMap<String, Integer>();
	}
	
	public UUID getPlayerUUID() {
		return playerUUID;
	}
	public Accessor getAccessor() {
		return accessor;
	}
	public BlockScanner getScanner() {
		return scanner;
	}
	public Variables getVariables() {
		return variables;
	}
	public Footsteps getFootsteps() {
		return footsteps;
	}
	public SoundPlayer getSoundPlayer() {
		return soundPlayer;
	}
	public MALogger getLogger() {
		return logger;
	}
	
	public int getCooldown(String key) {
		if(!cooldowns.containsKey(key)) return 0;
		
		return cooldowns.get(key);
	}
	public void setCooldown(String key, int value) {
		if(value<0) value = 0;
		
		cooldowns.put(key, value);
	}
	public int updateCooldown(String key) {
		if(!cooldowns.containsKey(key)) return 0;
		
		int value = cooldowns.get(key)-1;
		cooldowns.put(key, value);
		return value;
	}
}
