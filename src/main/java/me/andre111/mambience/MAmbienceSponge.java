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

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import com.google.inject.Inject;

import me.andre111.mambience.accessor.AccessorSponge;
import me.andre111.mambience.config.EngineConfig;

@Plugin(id = "mambience", name = "MAmbience", version = "0.3")
public class MAmbienceSponge {
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	
	@Inject
	private Logger ilogger;
	
	
	private MALogger logger;
	private MAScheduler scheduler;
	
	
	@Listener
    public void onServerStart(GameStartedServerEvent event) {
		logger = new MALogger(ilogger::info, ilogger::error);
		
		EngineConfig.initialize(logger, configDir.toFile());
		scheduler = new MAScheduler(logger, 1) {
			@Override
			public int getPlayerCount() {
				return Sponge.getServer().getOnlinePlayers().size();
			}
		};
		Task.Builder taskBuilder = Task.builder();
		taskBuilder.execute(scheduler).async().delayTicks(20).intervalTicks(20).submit(this);
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		scheduler.addPlayer(player.getUniqueId(), new AccessorSponge(player.getUniqueId()), logger);
	}
	
	@Listener
	public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
		Player player = event.getTargetEntity();
		scheduler.removePlayer(player.getUniqueId());
	}
	
	
	@Listener
	public void reload(GameReloadEvent event) {
	    // Do reload stuff
	}
}
