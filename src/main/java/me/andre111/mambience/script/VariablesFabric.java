package me.andre111.mambience.script;

import javax.script.Bindings;

import me.andre111.mambience.MAmbienceFabric;
import me.andre111.mambience.player.MAPlayer;
import me.andre111.mambience.scan.BlockScanner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;

public class VariablesFabric extends Variables {

	public void updateVariables(MAPlayer maplayer) {
		BlockScanner scanner = maplayer.getScanner();
		MAScriptEngine se = maplayer.getScriptEngine();
		
		ServerPlayerEntity player = MAmbienceFabric.server.getPlayerManager().getPlayer(maplayer.getPlayerUUID());
		ServerWorld world = player.getServerWorld();
		BlockPos location = player.getPos();
		int x = location.getX();
		int y = location.getY();
		int z = location.getZ();
		//BlockState block = world.getBlockState(location);
		BlockState headBlock = world.getBlockState(location.add(0, player.getSize(player.getPose()).height * 0.85F, 0)); //TODO: the *0.85f is taken from protected minecraft code
		
		boolean exposed = fastExposedCheck(world, location);
		//TODO: Slower more accurate exposed check
		//if(exposed) exposed = slowExposedCheck(world, location);
		
		Bindings bd = se.getEngineScopeBindings();
		//PLAYER
		{
			bd.put("__px", x);
			bd.put("__py", y);
			bd.put("__pz", z);
			//TODO: Player Dimension
			//bd.put("__pdim", );
			bd.put("__psunl", world.getLightLevel(LightType.SKY, location));
			bd.put("__pblockl", world.getLightLevel(LightType.BLOCK, location));
			bd.put("__pl", world.getLightLevel(location));
			//TODO: CanSeeSky, CanRainOn
			//bd.put("__pseesky", );
			//bd.put("__prainon", );
			
			bd.put("__pboat", (player.getRiddenEntity() != null && player.getRiddenEntity().getType() == EntityType.BOAT));
			bd.put("__psubm", (headBlock.getFluidState().getFluid().matchesType(Fluids.WATER)));
			bd.put("__pexpo", exposed);
			bd.put("__phealth", player.getHealth());
			bd.put("__pfood", player.getHungerManager().getFoodLevel());
		}
		//WORLD
		{
			bd.put("__wt", world.getTimeOfDay() % 24000);
			bd.put("__wrain", world.isRaining());
			bd.put("__wmoon", ((world.getTime()/24000) % 8));
			bd.put("__wbiome", Registry.BIOME.getId(world.getBiome(location)).toString());
		}
		//SCANNER
		{
			bd.put("__sblocks", scanner.getScanBlockCount());
			bd.put("__sbiomes", scanner.getScanBiomeCount());
			bd.put("__savgskylight", scanner.getAverageSkyLight());
			bd.put("__savglight", scanner.getAverageLight());
			bd.put("__savgtemp", scanner.getAverageTemperature());
			bd.put("__savghum", scanner.getAverageHumidity());
		}
		se.invokeFunction("Internal_Variables");
	}
	
	private static boolean fastExposedCheck(ServerWorld world, BlockPos location) {
		int mx = location.getX() + 1;
        int my = location.getY() + 1;
        int mz = location.getZ() + 1;
        
        for (int cx = mx - 2; cx <= mx; cx++) {
            for (int cy = my - 2; cy <= my; cy++) {
                for (int cz = mz - 2; cz <= mz; cz++) {
                	BlockPos offsetLocation = new BlockPos(cx, cy, cz);
                    if (!world.isAir(offsetLocation))
                        continue;
                    if (world.getLightLevel(LightType.SKY, offsetLocation) > 0)
                        return true;
                }
            }
        }
        return false;
	}
	//private static boolean slowExposedCheck(ServerWorld world, BlockPos location) {
	//	
	//}

}
