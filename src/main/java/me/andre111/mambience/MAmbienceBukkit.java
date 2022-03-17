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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import me.andre111.mambience.accessor.AccessorBukkit;
import me.andre111.mambience.resources.Generator;

//TODO: Missing important implementation parts:
// data loading! (somewhat abstracted system exists but needs bukkit implementation - will see if I find the time the contribute the required api)
// biome tags are currently not supported by spigot - but the existing code should work once that changes
public class MAmbienceBukkit extends JavaPlugin implements Listener {
	@Override
    public void onLoad() {
		MAmbience.init(new MALogger(getLogger()::info, getLogger()::warning), this.getDataFolder());
		
		Bukkit.getScheduler().runTaskTimer(this, MAmbience.getScheduler()::runTick, 1, 1);
		Bukkit.getPluginManager().registerEvents(this, this);
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "mambience:server");
		
		// extract datapack into folder for server to load it
		String targetPath = "./world/datapacks/";
		if(!Files.exists(Generator.getFilePath("data", targetPath))) {
			try {
				MAmbience.getLogger().log("Extracting datapack to default world, if this location has been changed please move the generated datapack accordingly!");
				MAmbience.getLogger().log("Please ensure that only one mambience datapack exists if you previously ran an older version of MAmbience.");
				Files.createDirectories(Paths.get(targetPath));
				Generator.generate("data", targetPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	
	// event listeners for implementing triggers
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockDamage(BlockDamageEvent event) {
		MAmbience.getScheduler().triggerEvents(event.getPlayer().getUniqueId(), MATrigger.ATTACK_BLOCK);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player player) {
			MAmbience.getScheduler().triggerEvents(player.getUniqueId(), MATrigger.ATTACK_HIT);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		switch(event.getAction()) {
		case LEFT_CLICK_AIR:
		case LEFT_CLICK_BLOCK:
			MAmbience.getScheduler().triggerEvents(event.getPlayer().getUniqueId(), MATrigger.ATTACK_SWING);
			break;
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			MAmbience.getScheduler().triggerEvents(event.getPlayer().getUniqueId(), event.getHand() == EquipmentSlot.HAND ? MATrigger.USE_ITEM_MAINHAND : MATrigger.USE_ITEM_OFFHAND);
			break;
		case PHYSICAL:
		default:
			break;
		}
	}
}
