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
package me.andre111.mambience;

import java.util.logging.Level;

import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.player.MAPlayerBukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MAmbienceBukkit extends JavaPlugin implements Listener {
	private MALogger logger;
	private MAScheduler scheduler;
	
	@Override
    public void onEnable() {
		logger = new MALogger() {
			@Override
			public void log(String s) {
				if(EngineConfig.DEBUGLOGGING) {
					getLogger().log(Level.INFO, s);
				}
			}

			@Override
			public void error(String s) {
				getLogger().log(Level.WARNING, s);
			}
		};
		
		
		EngineConfig.initialize(logger, this.getDataFolder());
		scheduler = new MAScheduler(logger, 1) {
			@Override
			public int getPlayerCount() {
				return Bukkit.getOnlinePlayers().size();
			}
		};
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, scheduler, 20, 20);
		Bukkit.getPluginManager().registerEvents(this, this);
    }
	
    @Override
    public void onDisable() {

    }
    
    @EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		scheduler.addPlayer(new MAPlayerBukkit(event.getPlayer(), logger));
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		scheduler.removePlayer(event.getPlayer().getUniqueId());
	}
}
