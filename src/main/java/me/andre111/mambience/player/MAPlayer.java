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

import java.util.HashMap;
import java.util.UUID;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.accessor.Accessor;
import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.scan.BlockScanner;

public class MAPlayer {
	private UUID playerUUID;
	private Accessor accessor;
	private BlockScanner scanner;
	private MALogger logger;
	private HashMap<String, Integer> cooldowns;
	
	public MAPlayer(UUID playerUUID, Accessor accessor, MALogger logger) {
		this.playerUUID = playerUUID;
		this.accessor = accessor;
		this.scanner = new BlockScanner(accessor, EngineConfig.SIZEX, EngineConfig.SIZEY, EngineConfig.SIZEZ);
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
