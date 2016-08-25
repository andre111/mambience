package me.andre111.mambience;

import java.util.logging.Level;

import me.andre111.mambience.config.EngineConfig;

import org.bukkit.plugin.java.JavaPlugin;

public class MAmbience extends JavaPlugin {
	
	@Override
    public void onEnable() {
		EngineConfig.initialize(this);
		new MAScheduler(this, 1);
    }
	
    @Override
    public void onDisable() {

    }
    
    public void log(String st) {
    	getLogger().log(Level.INFO, st);
    }
    public void error(String st) {
    	getLogger().log(Level.WARNING, st);
    }
}
