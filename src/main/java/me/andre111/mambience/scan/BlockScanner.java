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
package me.andre111.mambience.scan;

import java.util.HashMap;

import me.andre111.mambience.accessor.Accessor;

public final class BlockScanner {
	private Accessor accessor;
	private int xSize;
	private int ySize;
	private int zSize;
	private int currentYSize;
	private HashMap<String, Integer> blockCount = new HashMap<String, Integer>();
	private HashMap<String, Integer> biomeCount = new HashMap<String, Integer>();
	private double averageSkyLight;
	private double averageLight;
	private double averageTemperature;
	private double averageHumidity;
	private long lastScan;
	
	public BlockScanner(Accessor a, int xs, int ys, int zs) {
		accessor = a;
		xSize = xs;
		ySize = ys;
		zSize = zs;
		currentYSize = ySize;

		resetScanData();
		lastScan = 0;
	}
	
	public void performScan() {
		if(!accessor.updatePlayerInstance()) return; // fixes server crashes, TODO: but this should never even happen
		
		int startX = accessor.getX() - xSize/2;
		int startY = accessor.getY() - ySize/2;
		int startZ = accessor.getZ() - zSize/2;
		currentYSize = ySize;

		//TODO - should this move or cut the scanned area?
		if(startY < 0) {
			currentYSize -= startY;
			startY = 0;
		}
		if(startY+currentYSize > 256) {
			currentYSize = 256 - startY;
		}

		//Reset Scan Data
		resetScanData();

		//Perform Scan
		for(int xx=0; xx<xSize; xx++) {
			for(int zz=0; zz<zSize; zz++) {
				for(int yy=0; yy<currentYSize; yy++) {
					String id = accessor.getBlock(startX+xx, startY+yy, startZ+zz);
					blockCount.put(id, blockCount.containsKey(id) ? blockCount.get(id)+1 : 1);

					averageSkyLight += accessor.getSkyLight(startX+xx, startY+yy, startZ+zz);
					averageLight += accessor.getLight(startX+xx, startY+yy, startZ+zz);
				}

				String id = accessor.getBiome(startX+xx, 0, startZ+zz);
				biomeCount.put(id, biomeCount.containsKey(id) ? biomeCount.get(id)+1 : 1);

				averageTemperature += accessor.getTemperature(startX+xx, 0, startZ+zz);
				averageHumidity += accessor.getHumidity(startX+xx, 0, startZ+zz);
			}
		}

		averageSkyLight /= getScanBlockCount();
		averageLight /= getScanBlockCount();

		averageTemperature /= getScanBiomeCount();
		averageHumidity  /= getScanBiomeCount();
	}
	
	public void resetScanData() {
		blockCount.clear();
		biomeCount.clear();
		averageSkyLight = 0;
		averageLight = 0;
		averageTemperature = 0;
		averageHumidity = 0;
	}
	
	public int getxSize() {
		return xSize;
	}
	public void setxSize(int xSize) {
		this.xSize = xSize;
	}
	public int getySize() {
		return ySize;
	}
	public void setySize(int ySize) {
		this.ySize = ySize;
	}
	public int getzSize() {
		return zSize;
	}
	public void setzSize(int zSize) {
		this.zSize = zSize;
	}
	
	public int getScanBlockCount() {
		return xSize * currentYSize * zSize;
	}
	public HashMap<String, Integer> getScanBlockData() {
		return blockCount;
	}
	public int getScanBiomeCount() {
		return xSize * zSize;
	}
	public HashMap<String, Integer> getScanBiomeData() {
		return biomeCount;
	}
	public double getAverageSkyLight() {
		return averageSkyLight;
	}
	public double getAverageLight() {
		return averageLight;
	}
	public double getAverageTemperature() {
		return averageTemperature;
	}
	public double getAverageHumidity() {
		return averageHumidity;
	}

	public long getLastScan() {
		return lastScan;
	}
	public void setLastScan(long lastScan) {
		this.lastScan = lastScan;
	}
}
