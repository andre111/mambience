package me.andre111.mambience.scan;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockScanner {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	private Player player;
	private int xSize;
	private int ySize;
	private int zSize;
	private int currentYSize;
	private HashMap<Material, Integer> blockCount = new HashMap<Material, Integer>();
	private HashMap<Biome, Integer> biomeCount = new HashMap<Biome, Integer>();
	private double averageSkyLight;
	private double averageLight;
	private double averageTemperature;
	private double averageHumidity;
	private long lastScan;
	
	public BlockScanner(Player p, int xs, int ys, int zs) {
		player = p;
		xSize = xs;
		ySize = ys;
		zSize = zs;
		currentYSize = ySize;
		
		for(Material mat : Material.values()) {
			blockCount.put(mat, 0);
		}
		for(Biome biome : Biome.values()) {
			biomeCount.put(biome, 0);
		}
		lastScan = 0;
		averageSkyLight = 0;
		averageLight = 0;
		averageTemperature = 0;
		averageHumidity = 0;
	}
	
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
			currentYSize += startY;
			startY = 0;
		}
		
		//Reset Scan Data
		for(Material mat : Material.values()) {
			blockCount.put(mat, 0);
		}
		for(Biome biome : Biome.values()) {
			biomeCount.put(biome, 0);
		}
		averageSkyLight = 0;
		averageLight = 0;
		averageTemperature = 0;
		averageHumidity = 0;
		
		
		//Perform Scan
		Block block;
		Material foundMat;
		Biome foundBiome;
		for(int xx=0; xx<xSize; xx++) {
			for(int zz=0; zz<zSize; zz++) {
				for(int yy=0; yy<currentYSize; yy++) {
					block = world.getBlockAt(startX+xx, startY+yy, startZ+zz);
					foundMat = block.getType();
					blockCount.put(foundMat, blockCount.get(foundMat)+1);
					
					averageSkyLight += block.getLightFromSky();
					averageLight += block.getLightLevel();
				}
				
				foundBiome = world.getBiome(startX+xx, startZ+zz);
				biomeCount.put(foundBiome, biomeCount.get(foundBiome)+1);
				
				averageTemperature += world.getTemperature(startX+xx, startZ+zz);
				averageHumidity += world.getHumidity(startX+xx, startZ+zz);
			}
		}
		
		averageSkyLight /= getScanBlockCount();
		averageLight /= getScanBlockCount();
		
		averageTemperature /= getScanBiomeCount();
		averageHumidity  /= getScanBiomeCount();
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
	public HashMap<Material, Integer> getScanBlockData() {
		return blockCount;
	}
	public int getScanBiomeCount() {
		return xSize * zSize;
	}
	public HashMap<Biome, Integer> getScanBiomeData() {
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
