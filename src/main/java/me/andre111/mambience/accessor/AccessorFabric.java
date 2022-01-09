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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.andre111.mambience.MAmbience;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public abstract class AccessorFabric extends Accessor {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	protected PlayerEntity player;
	
	public AccessorFabric(UUID playerUUID) {
		super(playerUUID);
	}

	// Player related methods
	@Override
	public double getX() {
		return player.getX();
	}

	@Override
	public double getY() {
		return player.getY();
	}

	@Override
	public double getZ() {
		return player.getZ();
	}
	
	@Override
	public double getRotation() {
		return Math.toRadians(player.getYaw());
	}

	@Override
	public double getHealth() {
		return player.getHealth();
	}

	@Override
	public double getFoodLevel() {
		return player.getHungerManager().getFoodLevel();
	}

	@Override
	public boolean isSubmerged() {
		return player.isSubmergedIn(FluidTags.WATER);
	}
	
	@Override
	public boolean isSneaking() {
		return player.isSneaking();
	}

	@Override
	public boolean isJumping() {
		return !player.isOnGround() && player.getVehicle() == null && !player.isFallFlying() && !player.isClimbing();
	}

	@Override
	public boolean isOnGround() {
		return player.isOnGround();
	}
	
	@Override
	public String getArmor(int index) {
		// find correct stack
		Iterator<ItemStack> armorItems = player.getArmorItems().iterator();
		ItemStack itemstack = armorItems.next();
		for(int i=0; i<index; i++) itemstack = armorItems.next();
		
		// get item identifier
		Item item = itemstack.getItem();
		return item != null ? Registry.ITEM.getId(item).toString() : "";
	}

	// World related methods
	@Override
	public long getDayTime() {
		return player.getEntityWorld().getTimeOfDay() % 24000;
	}

	@Override
	public long getFullTime() {
		return player.getEntityWorld().getTime();
	}

	@Override
	public boolean isRaining() {
		return player.getEntityWorld().isRaining();
	}

	@Override
	public boolean isThundering() {
		return player.getEntityWorld().isThundering();
	}

	@Override
	public String getBlock(int x, int y, int z) {
		BlockState block = player.getEntityWorld().getBlockState(new BlockPos(x, y, z));
		
		return block != null ? Registry.BLOCK.getId(block.getBlock()).toString() : "";
	}

	@Override
	public String getBiome(int x, int y, int z) {
		Biome biome = player.getEntityWorld().getBiomeAccess().getBiome(new BlockPos(x, y, z));
		
		Registry<Biome> registry = player.getEntityWorld().getRegistryManager().get(Registry.BIOME_KEY);
		Identifier biomeId = registry.getId(biome);
		
		return (biomeId != null) ? biomeId.toString() : "";
	}
	
	@Override
	public String getDimension() {
		Registry<DimensionType> registry = player.getEntityWorld().getRegistryManager().get(Registry.DIMENSION_TYPE_KEY);
		Identifier dimensionId = registry.getId(player.getEntityWorld().getDimension());
		
		return (dimensionId != null) ? dimensionId.toString() : "";
	}

	@Override
	public int getLight(int x, int y, int z) {
		return player.getEntityWorld().getLightLevel(new BlockPos(x, y, z));
	}

	@Override
	public int getBlockLight(int x, int y, int z) {
		return player.getEntityWorld().getLightLevel(LightType.BLOCK, new BlockPos(x, y, z));
	}

	@Override
	public int getSkyLight(int x, int y, int z) {
		return player.getEntityWorld().getLightLevel(LightType.SKY, new BlockPos(x, y, z));
	}

	@Override
	public double getTemperature(int x, int y, int z) {
		return player.getEntityWorld().getBiomeAccess().getBiome(new BlockPos(x, y, z)).getTemperature();
	}

	@Override
	public double getHumidity(int x, int y, int z) {
		return player.getEntityWorld().getBiomeAccess().getBiome(new BlockPos(x, y, z)).getDownfall();
	}
	
	// Data related methods
	public List<String> getBlockTag(String name) {
		try {
			TagManager tagManager = player.getEntityWorld().getTagManager();
			Tag<Block> tag = tagManager.getTag(Registry.BLOCK_KEY, new Identifier(name), id -> new RuntimeException("Unknown Tag: " + id.toString()));
			List<String> blocks = new ArrayList<>();
			for(Block block : tag.values()) {
				blocks.add(Registry.BLOCK.getId(block).toString());
			}
			return blocks;
		} catch(Exception e) {
			MAmbience.getLogger().error("Error accessing tag: " + name + ": " + e.getMessage());
			return List.of();
		}
	}
	
	// helper method
	@SuppressWarnings("deprecation")
	protected <T extends ParticleEffect> T getParticleEffect(ParticleType<T> type, String parameters) {
		try {
			return type.getParametersFactory().read(type, new StringReader(parameters));
		} catch (CommandSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
