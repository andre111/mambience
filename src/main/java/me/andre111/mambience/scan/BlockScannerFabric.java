package me.andre111.mambience.scan;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public class BlockScannerFabric extends BlockScanner {
	//TODO: this shouldn't keep a reference to the player, at most its UUID
	private ServerPlayerEntity player;
	
	public BlockScannerFabric(ServerPlayerEntity p, int xs, int ys, int zs) {
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
		performScan(player.getServerWorld(), player.getPos());
	}
	private void performScan(ServerWorld world, BlockPos location) {
		performScan(world, location.getX(), location.getY(), location.getZ());
	}
	private void performScan(ServerWorld world, int xCenter, int yCenter, int zCenter) {
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
		BlockPos loc;
		BlockState block;
		Biome foundBiome;
		for(int xx=0; xx<xSize; xx++) {
			for(int zz=0; zz<zSize; zz++) {
				for(int yy=0; yy<currentYSize; yy++) {
					loc = new BlockPos(startX+xx, startY+yy, startZ+zz);
					block = world.getBlockState(loc);
					
					String id = Registry.BLOCK.getId(block.getBlock()).toString();
					blockCount.put(id, blockCount.containsKey(id) ? blockCount.get(id)+1 : 1);

					averageSkyLight += world.getLightLevel(LightType.SKY, loc);
					averageLight += world.getLightLevel(loc);
				}

				foundBiome = world.getBiome(new BlockPos(startX+xx, 0, startZ+zz));
				String id = Registry.BIOME.getId(foundBiome).toString();
				biomeCount.put(id, biomeCount.containsKey(id) ? biomeCount.get(id)+1 : 1);

				averageTemperature += foundBiome.getTemperature();
				averageHumidity += foundBiome.getRainfall();
			}
		}

		averageSkyLight /= getScanBlockCount();
		averageLight /= getScanBlockCount();

		averageTemperature /= getScanBiomeCount();
		averageHumidity  /= getScanBiomeCount();
	}
}
