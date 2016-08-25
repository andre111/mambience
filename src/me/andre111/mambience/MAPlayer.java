package me.andre111.mambience;

import java.util.HashMap;
import java.util.UUID;

import javax.script.CompiledScript;

import org.bukkit.entity.Player;

import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.scan.BlockScanner;
import me.andre111.mambience.script.MAScriptEngine;
import me.andre111.mambience.script.MAScripting;

public class MAPlayer {
	private UUID playerUUID;
	private BlockScanner scanner;
	private MAScriptEngine scriptEngine;
	private HashMap<String, Integer> cooldowns;
	private CompiledScript varSetterScript;
	
	public MAPlayer(MAmbience plugin, Player p) {
		playerUUID = p.getUniqueId();
		scanner = new BlockScanner(p, EngineConfig.SIZEX, EngineConfig.SIZEY, EngineConfig.SIZEZ);
		scriptEngine = MAScripting.newScriptEngine(plugin);
		cooldowns = new HashMap<String, Integer>();
	}
	
	public UUID getPlayerUUID() {
		return playerUUID;
	}
	public BlockScanner getScanner() {
		return scanner;
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
}
