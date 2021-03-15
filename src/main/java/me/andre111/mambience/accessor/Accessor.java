/*
 * Copyright (c) 2021 André Schweiger
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

public abstract class Accessor {
	protected UUID playerUUID;
	
	private boolean lastValuesInitialised = false;
	private String lastDimension;
	private double lastX;
	private double lastY;
	private double lastZ;
	
	public Accessor(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}
	
	public final void updateLastPosition() {
		this.lastValuesInitialised = true;
		this.lastDimension = getDimension();
		this.lastX = getX();
		this.lastY = getY();
		this.lastZ = getZ();
	}
	
	public final double getDeltaX() {
		if(!this.lastValuesInitialised) return 0;
		if(!this.lastDimension.equals(this.getDimension())) return 0;
		return this.getX() - this.lastX;
	}
	
	public final double getDeltaY() {
		if(!this.lastValuesInitialised) return 0;
		if(!this.lastDimension.equals(this.getDimension())) return 0;
		return this.getY() - this.lastY;
	}
	
	public final double getDeltaZ() {
		if(!this.lastValuesInitialised) return 0;
		if(!this.lastDimension.equals(this.getDimension())) return 0;
		return this.getZ() - this.lastZ;
	}
	
	// Player related methods
	public abstract boolean updatePlayerInstance();
	
	public abstract double getX();
	public abstract double getY();
	public abstract double getZ();
	
	public abstract double getRotation();

	public abstract double getHealth();
	public abstract double getFoodLevel();
	public abstract boolean isSubmerged();
	public abstract boolean isSneaking();
	public abstract boolean isJumping();
	public abstract boolean isOnGround();
	
	public abstract String getArmor(int index);
	
	// Sound related methods
	public abstract void playSound(String sound, float volume, float pitch);
	public abstract void playSound(String sound, double x, double y, double z, float volume, float pitch);
	public abstract void playGlobalFootstepSound(String sound, double x, double y, double z, float volume, float pitch);
	public abstract void stopSound(String sound);
	
	// Particle related methods
	public abstract void addParticle(String type, String parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
	
	// World related methods
	public abstract long getDayTime();
	public abstract long getFullTime();
	
	public abstract boolean isRaining();
	public abstract boolean isThundering();
	
	public abstract String getBlock(int x, int y, int z);
	public abstract String getBiome(int x, int y, int z);
	public abstract String getDimension();
	
	public abstract int getLight(int x, int y, int z);
	public abstract int getBlockLight(int x, int y, int z);
	public abstract int getSkyLight(int x, int y, int z);

	public abstract double getTemperature(int x, int y, int z);
	public abstract double getHumidity(int x, int y, int z);
}
