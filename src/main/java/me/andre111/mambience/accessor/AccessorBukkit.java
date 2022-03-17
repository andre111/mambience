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
package me.andre111.mambience.accessor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AccessorBukkit extends Accessor {
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
	public double getX() {
		return player.getLocation().getX();
	}

	@Override
	public double getY() {
		return player.getLocation().getY();
	}

	@Override
	public double getZ() {
		return player.getLocation().getZ();
	}

	@Override
	public double getRotation() {
		return Math.toRadians(player.getLocation().getYaw());
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
		return player.isInWater();
	}

	@Override
	public boolean isSneaking() {
		return player.isSneaking();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isJumping() {
		return !player.isOnGround() && player.getVehicle() == null && !player.isGliding() && !player.isClimbing();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOnGround() {
		return player.isOnGround();
	}

	@Override
	public String getArmor(int index) {
		ItemStack itemStack = player.getInventory().getArmorContents()[index];
		return itemStack != null ? itemStack.getType().getKey().toString() : "";
	}

	@Override
	public String getHeldItem(boolean mainHand) {
		ItemStack itemStack = mainHand ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
		return itemStack != null ? itemStack.getType().getKey().toString() : "";
	}

	// Sound related methods
	@Override
	public void playSound(String sound, float volume, float pitch) {
		player.playSound(player.getLocation(), sound, SoundCategory.AMBIENT, volume, pitch);
	}

	@Override
	public void playSound(String sound, double x, double y, double z, float volume, float pitch) {
		player.playSound(new Location(player.getWorld(), x, y, z), sound, SoundCategory.AMBIENT, volume, pitch);
	}

	@Override
	public void playGlobalSound(String sound, double x, double y, double z, float volume, float pitch) {
		player.getWorld().playSound(new Location(player.getWorld(), x, y, z), sound, SoundCategory.PLAYERS, volume, pitch);
	}

	@Override
	public void stopSound(String sound) {
		player.stopSound(sound, SoundCategory.AMBIENT);
	}

	// Particle related methods
	@Override
	public void addParticle(String type, String parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		//TODO: this is a limited implementation that requires custom code for every type - can this be solved any better?
		switch(type) {
		case "minecraft:block":
			player.spawnParticle(Particle.BLOCK_CRACK, x, y, z, 0, velocityX, velocityY, velocityZ, 1, Bukkit.createBlockData(parameters));
			break;
		case "minecraft:item":
			player.spawnParticle(Particle.ITEM_CRACK, x, y, z, 0, velocityX, velocityY, velocityZ, 1, new ItemStack(Bukkit.createBlockData(parameters).getMaterial()));
			break;
		case "minecraft:flame":
			player.spawnParticle(Particle.FLAME, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
			break;
		case "minecraft:lava":
			player.spawnParticle(Particle.LAVA, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
			break;
		case "minecraft:smoke":
			player.spawnParticle(Particle.SMOKE_NORMAL, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
			break;
		case "minecraft:end_rod":
			player.spawnParticle(Particle.END_ROD, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
			break;
		default:
			throw new RuntimeException("Particle Type not implemented: "+type);
		}
	}

	// World related methods
	@Override
	public long getDayTime() {
		return player.getWorld().getTime();
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
		return player.getWorld().getBlockAt(x, y, z).getType().getKey().toString();
	}

	@Override
	public String getBiome(int x, int y, int z) {
		return player.getWorld().getBiome(x, y, z).getKey().toString();
	}

	@Override
	public String getDimension() {
		return player.getWorld().getName();
	}

	@Override
	public int getLight(int x, int y, int z) {
		return player.getWorld().getBlockAt(x, y, z).getLightLevel();
	}

	@Override
	public int getBlockLight(int x, int y, int z) {
		return player.getWorld().getBlockAt(x, y, z).getLightFromBlocks();
	}

	@Override
	public int getSkyLight(int x, int y, int z) {
		return player.getWorld().getBlockAt(x, y, z).getLightFromSky();
	}

	@Override
	public double getTemperature(int x, int y, int z) {
		return player.getWorld().getTemperature(x, y, z);
	}

	@Override
	public double getHumidity(int x, int y, int z) {
		return player.getWorld().getHumidity(x, y, z);
	}

	// Data related methods
	@Override
	public List<String> getBlockTag(String name) {
		Tag<Material> tag = Bukkit.getTag("blocks", NamespacedKey.fromString(name), Material.class);
		return tag != null ? tag.getValues().stream().map(m -> m.getKey().toString()).collect(Collectors.toList()) : List.of();
	}

	@Override
	public List<String> getBiomeTag(String name) {
		Tag<Biome> tag = Bukkit.getTag("biomes", NamespacedKey.fromString(name), Biome.class);
		return tag != null ? tag.getValues().stream().map(m -> m.getKey().toString()).collect(Collectors.toList()) : List.of();
	}

	@Override
	public List<String> getItemTag(String name) {
		Tag<Material> tag = Bukkit.getTag("items", NamespacedKey.fromString(name), Material.class);
		return tag != null ? tag.getValues().stream().map(m -> m.getKey().toString()).collect(Collectors.toList()) : List.of();
	}

}
