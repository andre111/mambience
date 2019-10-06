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

import java.util.UUID;

import me.andre111.mambience.MAmbienceFabric;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public class AccessorFabric extends Accessor {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	private ServerPlayerEntity player;
	
	public AccessorFabric(UUID playerUUID) {
		super(playerUUID);
	}

	// Player related methods
	@Override
	public boolean updatePlayerInstance() {
		player = MAmbienceFabric.server.getPlayerManager().getPlayer(playerUUID);
		return player != null;
	}

	@Override
	public int getX() {
		return player.getBlockPos().getX();
	}

	@Override
	public int getY() {
		return player.getBlockPos().getY();
	}

	@Override
	public int getZ() {
		return player.getBlockPos().getZ();
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
		return player.isSubmergedIn(FluidTags.WATER, true);
	}

	// World related methods
	@Override
	public long getDayTime() {
		return player.getServerWorld().getTimeOfDay() % 24000;
	}

	@Override
	public long getFullTime() {
		return player.getServerWorld().getTime();
	}

	@Override
	public boolean isRaining() {
		return player.getServerWorld().isRaining();
	}

	@Override
	public String getBlock(int x, int y, int z) {
		BlockState block = player.getServerWorld().getBlockState(new BlockPos(x, y, z));
		
		return Registry.BLOCK.getId(block.getBlock()).toString();
	}

	@Override
	public String getBiome(int x, int y, int z) {
		Biome biome = player.getServerWorld().getBiome(new BlockPos(x, y, z));
		
		return Registry.BIOME.getId(biome).toString();
	}

	@Override
	public int getLight(int x, int y, int z) {
		return player.getServerWorld().getLightLevel(new BlockPos(x, y, z));
	}

	@Override
	public int getBlockLight(int x, int y, int z) {
		return player.getServerWorld().getLightLevel(LightType.BLOCK, new BlockPos(x, y, z));
	}

	@Override
	public int getSkyLight(int x, int y, int z) {
		return player.getServerWorld().getLightLevel(LightType.SKY, new BlockPos(x, y, z));
	}

	@Override
	public double getTemperature(int x, int y, int z) {
		return player.getServerWorld().getBiome(new BlockPos(x, y, z)).getTemperature();
	}

	@Override
	public double getHumidity(int x, int y, int z) {
		return player.getServerWorld().getBiome(new BlockPos(x, y, z)).getRainfall();
	}
}
