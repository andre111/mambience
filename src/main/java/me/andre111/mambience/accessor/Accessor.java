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
package me.andre111.mambience.accessor;

import java.util.UUID;

public abstract class Accessor {
	protected UUID playerUUID;
	
	public Accessor(UUID playerUUID) {
		this.playerUUID = playerUUID;
		updatePlayerInstance();
	}
	
	// Player related methods
	public abstract boolean updatePlayerInstance();
	
	public abstract int getX();
	public abstract int getY();
	public abstract int getZ();

	public abstract double getHealth();
	public abstract double getFoodLevel();
	public abstract boolean isSubmerged();
	
	public abstract void playSound(String sound, float volume, float pitch);
	public abstract void stopSound(String sound);
	
	// World related methods
	public abstract long getDayTime();
	public abstract long getFullTime();
	
	public abstract boolean isRaining();
	
	public abstract String getBlock(int x, int y, int z);
	public abstract String getBiome(int x, int y, int z);

	public abstract int getLight(int x, int y, int z);
	public abstract int getBlockLight(int x, int y, int z);
	public abstract int getSkyLight(int x, int y, int z);

	public abstract double getTemperature(int x, int y, int z);
	public abstract double getHumidity(int x, int y, int z);
}
