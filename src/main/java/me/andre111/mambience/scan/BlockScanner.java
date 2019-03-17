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
