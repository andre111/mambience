package me.andre111.mambience.scan;

import java.util.Optional;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.property.block.GroundLuminanceProperty;
import org.spongepowered.api.data.property.block.SkyLuminanceProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;

public class BlockScannerSponge extends BlockScanner {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	private Player player;

	public BlockScannerSponge(Player p, int xs, int ys, int zs) {
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
	private void performScan(Location<World> location) {
		performScan(location.getExtent(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
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
		resetScanData();

		//Perform Scan
		Location<World> loc;
		BlockState block;
		BiomeType foundBiome;
		for(int xx=0; xx<xSize; xx++) {
			for(int zz=0; zz<zSize; zz++) {
				for(int yy=0; yy<currentYSize; yy++) {
					block = world.getBlock(startX+xx, startY+yy, startZ+zz);
					loc = world.getLocation(startX+xx, startY+yy, startZ+zz);
					
					String id = block.getType().getId();
					if(!id.contains(":")) id = "minecraft:"+id;
					blockCount.put(id, blockCount.containsKey(id) ? blockCount.get(id)+1 : 1);

					Optional<SkyLuminanceProperty> sl = loc.getProperty(SkyLuminanceProperty.class);
					if(sl.isPresent()) {
						averageSkyLight += sl.get().getValue();
					}
					
					Optional<GroundLuminanceProperty> l = loc.getProperty(GroundLuminanceProperty.class);
					if(l.isPresent()) {
						averageLight += l.get().getValue();
					}
				}

				foundBiome = world.getBiome(startX+xx, 0, startZ+zz);
				String id = foundBiome.getId();
				if(!id.contains(":")) id = "minecraft:"+id;
				biomeCount.put(id, biomeCount.containsKey(id) ? biomeCount.get(id)+1 : 1);

				averageTemperature += foundBiome.getTemperature();
				averageHumidity += foundBiome.getHumidity();
			}
		}

		averageSkyLight /= getScanBlockCount();
		averageLight /= getScanBlockCount();

		averageTemperature /= getScanBiomeCount();
		averageHumidity  /= getScanBiomeCount();
	}
}
