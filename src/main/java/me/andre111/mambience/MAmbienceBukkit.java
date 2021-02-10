/*
 * Copyright (c) 2021 André Schweiger
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

import me.andre111.mambience.accessor.AccessorBukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MAmbienceBukkit extends JavaPlugin implements Listener {

	@Override
    public void onEnable() {
		MAmbience.init(new MALogger(getLogger()::info, getLogger()::warning), this.getDataFolder());
		
		Bukkit.getScheduler().runTaskTimer(this, MAmbience.getScheduler()::runSyncUpdate, 1, 1);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, MAmbience.getScheduler()::runAsyncUpdate, 20, 20);
		Bukkit.getPluginManager().registerEvents(this, this);
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "mambience:server");
    }
	
    @Override
    public void onDisable() {

    }
    
    @EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		MAmbience.addPlayer(event.getPlayer().getUniqueId(), new AccessorBukkit(event.getPlayer().getUniqueId()));
	}
	
	@EventHandler
	public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
		if(event.getChannel().equals("mambience:server")) {
			// send notify payload (mambience:server channel with "enabled" message)
			event.getPlayer().sendPluginMessage(this, "mambience:server", "enabled".getBytes());
		}
	}
}
