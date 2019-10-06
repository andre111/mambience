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

import java.util.HashMap;

public abstract class BlockScanner {
	protected int xSize;
	protected int ySize;
	protected int zSize;
	protected int currentYSize;
	protected HashMap<String, Integer> blockCount = new HashMap<String, Integer>();
	protected HashMap<String, Integer> biomeCount = new HashMap<String, Integer>();
	protected double averageSkyLight;
	protected double averageLight;
	protected double averageTemperature;
	protected double averageHumidity;
	protected long lastScan;
	
	public abstract void performScan();
	
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
