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

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.GroundLuminanceProperty;
import org.spongepowered.api.data.property.block.SkyLuminanceProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.weather.Weathers;

public class AccessorSponge extends Accessor {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	private Player player;
	
	public AccessorSponge(UUID playerUUID) {
		super(playerUUID);
	}

	// Player related methods
	@Override
	public boolean updatePlayerInstance() {
		Optional<Player> optPlayer = Sponge.getServer().getPlayer(playerUUID);
		player = optPlayer.isPresent() ? optPlayer.get() : null;
		return player != null;
	}

	@Override
	public int getX() {
		return player.getLocation().getBlockX();
	}

	@Override
	public int getY() {
		return player.getLocation().getBlockY();
	}

	@Override
	public int getZ() {
		return player.getLocation().getBlockZ();
	}

	@Override
	public double getHealth() {
		return player.health().get();
	}

	@Override
	public double getFoodLevel() {
		return player.foodLevel().get();
	}

	@Override
	public boolean isSubmerged() {
		BlockState headBlock = player.getLocation().add(0, 1.62, 0).getBlock();
		
		return headBlock.getType()==BlockTypes.WATER || headBlock.getType()==BlockTypes.FLOWING_WATER;
	}

	// World related methods
	@Override
	public long getDayTime() {
		return player.getWorld().getProperties().getWorldTime() % 24000;
	}

	@Override
	public long getFullTime() {
		return player.getWorld().getProperties().getTotalTime();
	}

	@Override
	public boolean isRaining() {
		return player.getWorld().getWeather().equals(Weathers.RAIN) || player.getWorld().getWeather().equals(Weathers.THUNDER_STORM);
	}

	@Override
	public String getBlock(int x, int y, int z) {
		BlockState block = player.getWorld().getBlock(x, y, z);
		
		String id = block.getType().getId();
		if(!id.contains(":")) id = "minecraft:"+id;
		
		return id;
	}

	@Override
	public String getBiome(int x, int y, int z) {
		BiomeType biome = player.getWorld().getBiome(x, y, z);
		
		String id = biome.getId();
		if(!id.contains(":")) id = "minecraft:"+id;
		
		return id;
	}

	@Override
	public int getLight(int x, int y, int z) {
		Location<World> loc = player.getWorld().getLocation(x, y, z);

		Optional<GroundLuminanceProperty> sl = loc.getProperty(GroundLuminanceProperty.class); //TODO: this is wrong?
		if(sl.isPresent()) {
			return sl.get().getValue().intValue();
		}
		return 0;
	}

	@Override
	public int getBlockLight(int x, int y, int z) {
		Location<World> loc = player.getWorld().getLocation(x, y, z);

		Optional<GroundLuminanceProperty> sl = loc.getProperty(GroundLuminanceProperty.class);
		if(sl.isPresent()) {
			return sl.get().getValue().intValue();
		}
		return 0;
	}

	@Override
	public int getSkyLight(int x, int y, int z) {
		Location<World> loc = player.getWorld().getLocation(x, y, z);

		Optional<SkyLuminanceProperty> sl = loc.getProperty(SkyLuminanceProperty.class);
		if(sl.isPresent()) {
			return sl.get().getValue().intValue();
		}
		return 0;
	}

	@Override
	public double getTemperature(int x, int y, int z) {
		return player.getWorld().getBiome(x, y, z).getTemperature();
	}

	@Override
	public double getHumidity(int x, int y, int z) {
		return player.getWorld().getBiome(x, y, z).getHumidity();
	}
}
