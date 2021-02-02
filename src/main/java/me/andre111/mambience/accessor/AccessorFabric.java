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

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public abstract class AccessorFabric extends Accessor {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	protected PlayerEntity player;
	
	public AccessorFabric(UUID playerUUID) {
		super(playerUUID);
	}

	// Player related methods
	@Override
	public int getX() {
		return (int) player.getX();
	}

	@Override
	public int getY() {
		return (int) player.getY();
	}

	@Override
	public int getZ() {
		return (int) player.getZ();
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
		
		return Registry.BLOCK.getId(block.getBlock()).toString();
	}

	@Override
	public String getBiome(int x, int y, int z) {
		Biome biome = player.getEntityWorld().getBiomeAccess().getBiome(new BlockPos(x, y, z));
		
		Registry<Biome> registry = player.getEntityWorld().getRegistryManager().get(Registry.BIOME_KEY);
		return registry.getId(biome).toString();
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
	
	// helper method
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
