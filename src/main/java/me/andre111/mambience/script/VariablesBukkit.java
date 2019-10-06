package me.andre111.mambience.script;

import javax.script.Bindings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.andre111.mambience.player.MAPlayer;
import me.andre111.mambience.scan.BlockScanner;

public class VariablesBukkit extends Variables {
	@Override
	public void updateVariables(MAPlayer maplayer) {
		BlockScanner scanner = maplayer.getScanner();
		MAScriptEngine se = maplayer.getScriptEngine();
		
		Player player = Bukkit.getPlayer(maplayer.getPlayerUUID());
		if(player == null) return; // fixes server crashes, but TODO: this should never even happen
		World world = player.getWorld();
		Location location = player.getLocation();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		Block block = location.getBlock();
		Block headBlock = player.getEyeLocation().getBlock();
		
		boolean exposed = fastExposedCheck(location);
		//TODO: Slower more accurate exposed check
		//if(exposed) exposed = slowExposedCheck(location);
		
		Bindings bd = se.getEngineScopeBindings();
		//PLAYER
		{
			bd.put("__px", x);
			bd.put("__py", y);
			bd.put("__pz", z);
			//TODO: Player Dimension
			//bd.put("__pdim", );
			bd.put("__psunl", block.getLightFromSky());
			bd.put("__pblockl", block.getLightFromBlocks());
			bd.put("__pl", block.getLightLevel());
			//TODO: CanSeeSky, CanRainOn
			//bd.put("__pseesky", );
			//bd.put("__prainon", );
			bd.put("__pboat", (player.getVehicle() != null && player.getVehicle().getType() == EntityType.BOAT));
			bd.put("__psubm", hasWater(headBlock));
			bd.put("__pexpo", exposed);
			bd.put("__phealth", player.getHealth());
			bd.put("__pfood", player.getFoodLevel());
		}
		//WORLD
		{
			bd.put("__wt", world.getTime());
			bd.put("__wrain", world.hasStorm());
			bd.put("__wmoon", ((world.getFullTime()/24000) % 8));
			//TODO: bukkit biome names just seem to be the internal minecraft one uppercased
			bd.put("__wbiome", "minecraft:"+world.getBiome(x, z).name().toLowerCase());
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
	
	private static boolean fastExposedCheck(Location location) {
		int mx = location.getBlockX() + 1;
        int my = location.getBlockY() + 1;
        int mz = location.getBlockZ() + 1;
        
        for (int cx = mx - 2; cx <= mx; cx++) {
            for (int cy = my - 2; cy <= my; cy++) {
                for (int cz = mz - 2; cz <= mz; cz++) {
                	Block block = location.getWorld().getBlockAt(cx, cy, cz);
                    if (block.getType()!=Material.AIR)
                        continue;
                    if (block.getLightFromSky() > 4) // increased from 0
                        return true;
                }
            }
        }
        return false;
	}
	//private static boolean slowExposedCheck(Location location) {
	//	
	//}
	
	private static boolean hasWater(Block block) {
		return (block.getType()==Material.WATER || block.getType()==Material.BUBBLE_COLUMN 
				|| block.getType()==Material.KELP || block.getType()==Material.KELP_PLANT 
				|| block.getType()==Material.SEAGRASS || block.getType()==Material.TALL_SEAGRASS 
				|| (block.getBlockData() instanceof Waterlogged && ((Waterlogged) block.getBlockData()).isWaterlogged()));
	}
}
