/*
 * Copyright (c) 2023 Andre Schweiger
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import me.andre111.mambience.MAmbienceFabric;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList.Named;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class AccessorFabricServer extends AccessorFabric {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	private ServerPlayerEntity serverPlayer;
	
	public AccessorFabricServer(UUID playerUUID) {
		super(playerUUID);
	}

	// Player related methods
	@Override
	public boolean updatePlayerInstance() {
		player = serverPlayer = MAmbienceFabric.server != null ? MAmbienceFabric.server.getPlayerManager().getPlayer(playerUUID) : null;
		return player != null;
	}

	// Sound related methods
	@Override
	public void playSound(String sound, float volume, float pitch) {
		if(serverPlayer == null) return;
		
		serverPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(SoundEvent.of(new Identifier(sound))), SoundCategory.AMBIENT, player.getX(), player.getY(), player.getZ(), volume, pitch, serverPlayer.getRandom().nextLong()));
	}

	@Override
	public void playSound(String sound, double x, double y, double z, float volume, float pitch) {
		if(serverPlayer == null) return;
		
		serverPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(SoundEvent.of(new Identifier(sound))), SoundCategory.AMBIENT, x, y, z, volume, pitch, serverPlayer.getRandom().nextLong()));
	}
	

	@Override
	public void playGlobalSound(String sound, double x, double y, double z, float volume, float pitch) {
		if(serverPlayer == null) return;
		
		long seed = serverPlayer.getRandom().nextLong();
		for(ServerPlayerEntity other : serverPlayer.getServerWorld().getPlayers()) {
			// check for same dimension and within audible distance
			if(other.getEntityWorld().equals(serverPlayer.getWorld()) && other.getPos().squaredDistanceTo(serverPlayer.getPos()) < 16*16) {
				other.networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(SoundEvent.of(new Identifier(sound))), SoundCategory.PLAYERS, x, y, z, volume, pitch, seed));
			}
		}
	}

	@Override
	public void stopSound(String sound) {
		if(serverPlayer == null) return;
		
		serverPlayer.networkHandler.sendPacket(new StopSoundS2CPacket(new Identifier(sound), SoundCategory.AMBIENT));
	}

	// Particle related methods
	@Override
	public void addParticle(String type, String parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		if(serverPlayer == null || serverPlayer.getWorld() == null) return;
		
		ParticleType<?> ptype = Registries.PARTICLE_TYPE.get(new Identifier(type));
		ParticleEffect particle = getParticleEffect(ptype, " "+parameters);
		if(particle != null) {
			serverPlayer.getServerWorld().spawnParticles(serverPlayer, particle, false, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
		}
	}

	@Override
	protected <T> List<Identifier> getTagEntries(RegistryKey<? extends Registry<T>> key, Identifier id) {
		TagKey<T> tagKey = TagKey.of(key, id);
		Optional<Named<T>> optional = serverPlayer.getServer().getRegistryManager().get(key).getEntryList(tagKey);
		if(optional.isPresent()) {
			return optional.get().stream().map(entry -> entry.getKey().get().getValue()).collect(Collectors.toList());
		} else {
			return List.of();
		}
	}
}
