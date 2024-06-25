/*
 * Copyright (c) 2024 Andre Schweiger
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

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.andre111.mambience.MAmbience;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
		return item != null ? Registries.ITEM.getId(item).toString() : "";
	}
	
	@Override
	public String getHeldItem(boolean mainHand) {
		ItemStack itemstack = mainHand ? player.getMainHandStack() : player.getOffHandStack();

		// get item identifier
		Item item = itemstack.getItem();
		return item != null ? Registries.ITEM.getId(item).toString() : "";
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
		
		return block != null ? Registries.BLOCK.getId(block.getBlock()).toString() : "";
	}

	@Override
	public String getBiome(int x, int y, int z) {
		return player.getEntityWorld().getBiome(new BlockPos(x, y, z)).getKey().map(key -> key.getValue().toString()).orElse("");
	}
	
	@Override
	public String getDimension() {
		Registry<DimensionType> registry = player.getEntityWorld().getRegistryManager().get(RegistryKeys.DIMENSION_TYPE);
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
		Registry<Biome> registry = player.getEntityWorld().getRegistryManager().get(RegistryKeys.BIOME);
		return player.getEntityWorld().getBiome(new BlockPos(x, y, z)).getKey().map(key -> registry.get(key).getTemperature()).orElse(0f);
	}

	@Override
	public Stream<String> getEntities(double x, double y, double z, double xSize, double ySize, double zSize) {
		List<Entity> entities = player.getEntityWorld().getOtherEntities(player, new Box(x-xSize/2.0, y-ySize/2.0, z-zSize/2.0, x+xSize/2, y+ySize/2, z+zSize/2));
		return entities.stream().map(entity -> EntityType.getId(entity.getType()).toString());
	}
	
	// Data related methods
	@Override
	public List<String> getBlockTag(String name) {
		return getTag(RegistryKeys.BLOCK, name);
	}
	
	@Override
	public List<String> getBiomeTag(String name) {
		return getTag(RegistryKeys.BIOME, name);
	}
	
	@Override
	public List<String> getItemTag(String name) {
		return getTag(RegistryKeys.ITEM, name);
	}
	
	@Override
	public List<String> getEntityTag(String name) {
		return getTag(RegistryKeys.ENTITY_TYPE, name);
	}
	
	private <T> List<String> getTag(RegistryKey<? extends Registry<T>> key, String name) {
		// this whole implementation is not the most efficient - but as the returned lists are cached it is acceptable
		try {
			List<Identifier> tagEntries = getTagEntries(key, Identifier.of(name));
			return tagEntries.stream().map(id -> id.toString()).collect(Collectors.toList());
		} catch(Exception e) {
			MAmbience.getLogger().error("Error accessing tag: " + name + ": " + e.getMessage());
			return List.of();
		}
	}
	
	protected abstract <T> List<Identifier> getTagEntries(RegistryKey<? extends Registry<T>> key, Identifier id);

	// helper method
	protected ParticleEffect getParticleEffect(String type, String parameters) {
		String name = type;
		switch(type) {
		case "minecraft:item":
			name = name + "{item:\"" + parameters + "\"}";
			break;
		case "minecraft:block":
			name = name + "{block_state:\"" + parameters + "\"}";
			break;
		}
		
		ParticleEffectArgumentType parser = new ParticleEffectArgumentType(CommandManager.createRegistryAccess(player.getEntityWorld().getRegistryManager())); 
		try {
			return parser.parse(new StringReader(name));
		} catch (CommandSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
