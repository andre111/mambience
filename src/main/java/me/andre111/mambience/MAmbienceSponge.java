/*
 * Copyright (c) 2020 André Schweiger
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
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ChannelRegistrationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import com.google.inject.Inject;

import me.andre111.mambience.accessor.AccessorSponge;

@Plugin(id = "mambience", name = "MAmbience", version = "0.3")
public class MAmbienceSponge {
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

	@Inject
	private Logger ilogger;
	

	@Inject
	private Game game;
    private ChannelBinding.RawDataChannel rawDataChannel;
	
	@Listener
    public void onServerStart(GameStartedServerEvent event) {
		MAmbience.init(new MALogger(ilogger::info, ilogger::error), configDir.toFile());
		
		Task.Builder taskBuilder = Task.builder();
		taskBuilder.execute(MAmbience.getScheduler()::runSyncUpdate).delayTicks(1).intervalTicks(1).submit(this);
		taskBuilder.execute(MAmbience.getScheduler()::runAsyncUpdate).async().delayTicks(20).intervalTicks(20).submit(this);
		
		rawDataChannel = game.getChannelRegistrar().createRawChannel(this, "mambience:server");
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		MAmbience.addPlayer(player.getUniqueId(), new AccessorSponge(player.getUniqueId()));
	}
	
	@Listener
	public void onChannelRegistration(ChannelRegistrationEvent.Register event) {
		if(event.getChannel().equals("mambience:server")) {
			// send notify payload (mambience:server channel with "enabled" message)
			Player player = event.getCause().first(Player.class).get();
			rawDataChannel.sendTo(player, buf -> buf.writeBytes("enabled".getBytes()));
		}
	}
	
	
	@Listener
	public void reload(GameReloadEvent event) {
	    // Do reload stuff
	}
}
