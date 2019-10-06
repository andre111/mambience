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

import javax.script.CompiledScript;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.scan.BlockScanner;
import me.andre111.mambience.script.MAScriptEngine;
import me.andre111.mambience.script.MAScripting;

public abstract class MAPlayer {
	private UUID playerUUID;
	private Accessor accessor;
	private BlockScanner scanner;
	private MALogger logger;
	private MAScriptEngine scriptEngine;
	private HashMap<String, Integer> cooldowns;
	private CompiledScript varSetterScript;
	
	public MAPlayer(UUID playerUUID, Accessor accessor, MALogger logger) {
		this.playerUUID = playerUUID;
		this.accessor = accessor;
		this.scanner = new BlockScanner(accessor, EngineConfig.SIZEX, EngineConfig.SIZEY, EngineConfig.SIZEZ);
		this.logger = logger;
		this.scriptEngine = MAScripting.newScriptEngine(logger);
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
	public MAScriptEngine getScriptEngine() {
		return scriptEngine;
	}
	
	public int getCooldown(String key) {
		if(!cooldowns.containsKey(key)) return 0;
		
		return cooldowns.get(key);
	}
	public void setCooldown(String key, int value) {
		if(value<0) value = 0;
		
		cooldowns.put(key, value);
	}

	public CompiledScript getVarSetterScript() {
		return varSetterScript;
	}
	public void setVarSetterScript(CompiledScript varSetterScript) {
		this.varSetterScript = varSetterScript;
	}
	
	public abstract void playSound(String sound, float volume, float pitch);
	public abstract void stopSound(String sound);
}
