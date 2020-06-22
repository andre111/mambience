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
package me.andre111.mambience.accessor;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;

public class AccessorBukkit extends Accessor {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	private Player player;

	public AccessorBukkit(UUID playerUUID) {
		super(playerUUID);
	}

	// Player related methods
	@Override
	public boolean updatePlayerInstance() {
		player = Bukkit.getPlayer(playerUUID);
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
		return player.getHealth();
	}

	@Override
	public double getFoodLevel() {
		return player.getFoodLevel();
	}

	@Override
	public boolean isSubmerged() {
		Block headBlock = player.getEyeLocation().getBlock();
		
		return (headBlock.getType()==Material.WATER || headBlock.getType()==Material.BUBBLE_COLUMN 
				|| headBlock.getType()==Material.KELP || headBlock.getType()==Material.KELP_PLANT 
				|| headBlock.getType()==Material.SEAGRASS || headBlock.getType()==Material.TALL_SEAGRASS 
				|| (headBlock.getBlockData() instanceof Waterlogged && ((Waterlogged) headBlock.getBlockData()).isWaterlogged()));
	}

	@Override
	public void playSound(String sound, float volume, float pitch) {
		player.playSound(player.getLocation(), sound, SoundCategory.AMBIENT, volume, pitch);
	}

	@Override
	public void stopSound(String sound) {
		player.stopSound(sound);
	}

	// World related methods
	@Override
	public long getDayTime() {
		return player.getWorld().getTime() % 24000;
	}

	@Override
	public long getFullTime() {
		return player.getWorld().getFullTime();
	}

	@Override
	public boolean isRaining() {
		return player.getWorld().hasStorm();
	}
	
	@Override
	public boolean isThundering() {
		return player.getWorld().isThundering();
	}

	@Override
	public String getBlock(int x, int y, int z) {
		Block block = player.getWorld().getBlockAt(x, y, z);
		
		//TODO: bukkit block names just seem to be the internal minecraft one uppercased
		return "minecraft:"+block.getType().name().toLowerCase();
	}

	@Override
	public String getBiome(int x, int y, int z) {
		Biome biome = player.getWorld().getBiome(x, z);
		
		//TODO: bukkit biome names just seem to be the internal minecraft one uppercased
		return "minecraft:"+biome.name().toLowerCase();
	}

	@Override
	public int getLight(int x, int y, int z) {
		Block block = player.getWorld().getBlockAt(x, y, z);
		
		return block.getLightLevel();
	}

	@Override
	public int getBlockLight(int x, int y, int z) {
		Block block = player.getWorld().getBlockAt(x, y, z);
		
		return block.getLightFromBlocks();
	}

	@Override
	public int getSkyLight(int x, int y, int z) {
		Block block = player.getWorld().getBlockAt(x, y, z);
		
		return block.getLightFromSky();
	}

	@Override
	public double getTemperature(int x, int y, int z) {
		return player.getWorld().getTemperature(x, z);
	}

	@Override
	public double getHumidity(int x, int y, int z) {
		return player.getWorld().getHumidity(x, z);
	}
}
