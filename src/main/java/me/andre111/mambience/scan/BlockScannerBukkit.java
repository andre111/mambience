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
package me.andre111.mambience.scan;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockScannerBukkit extends BlockScanner {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	private Player player;

	public BlockScannerBukkit(Player p, int xs, int ys, int zs) {
		player = p;
		xSize = xs;
		ySize = ys;
		zSize = zs;
		currentYSize = ySize;

		resetScanData();
		lastScan = 0;
	}

	@Override
	public void performScan() {
		performScan(player.getLocation());
	}
	private void performScan(Location location) {
		performScan(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	private void performScan(World world, int xCenter, int yCenter, int zCenter) {
		int startX = xCenter - xSize/2;
		int startY = yCenter - ySize/2;
		int startZ = zCenter - zSize/2;
		currentYSize = ySize;

		//TODO - should this move or cut the scanned area?
		if(startY<0) {
			currentYSize -= startY;
			startY = 0;
		}

		//Reset Scan Data
		resetScanData();

		//Perform Scan
		Block block;
		Material foundMat;
		Biome foundBiome;
		for(int xx=0; xx<xSize; xx++) {
			for(int zz=0; zz<zSize; zz++) {
				for(int yy=0; yy<currentYSize; yy++) {
					block = world.getBlockAt(startX+xx, startY+yy, startZ+zz);
					foundMat = block.getType();
					
					//TODO: bukkit block names just seem to be the internal minecraft one uppercased
					String id ="minecraft:"+foundMat.name().toLowerCase();
					blockCount.put(id, blockCount.containsKey(id) ? blockCount.get(id)+1 : 1);

					averageSkyLight += block.getLightFromSky();
					averageLight += block.getLightLevel();
				}

				foundBiome = world.getBiome(startX+xx, startZ+zz);
				//TODO: bukkit biome names just seem to be the internal minecraft one uppercased
				String id = "minecraft:"+foundBiome.name().toLowerCase();
				biomeCount.put(id, biomeCount.containsKey(id) ? biomeCount.get(id)+1 : 1);

				averageTemperature += world.getTemperature(startX+xx, startZ+zz);
				averageHumidity += world.getHumidity(startX+xx, startZ+zz);
			}
		}

		averageSkyLight /= getScanBlockCount();
		averageLight /= getScanBlockCount();

		averageTemperature /= getScanBiomeCount();
		averageHumidity  /= getScanBiomeCount();
	}
}
