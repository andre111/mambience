package me.andre111.mambience.script;

import java.util.ArrayList;

import javax.script.Bindings;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.scan.BlockScanner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Variables {
	private static ArrayList<Variable> variables = new ArrayList<Variable>();
	
	public static void init(MAPlayer maplayer) {
		MAScriptEngine se = maplayer.getScriptEngine();
		
		se.evalJS("Player = {};");
		se.evalJS("World = {};");
		se.evalJS("Scanner = {};");
		se.evalJS("Internal = {};");
		se.evalJS("Internal.Cooldown = {};");
		se.evalJS("Internal.Function = {};");
		
		maplayer.setVarSetterScript(
				se.compileScript("function Internal_Variables() {"
					//PLAYER
						+ "   Player.X = __px;"
						+ "   Player.Y = __py;"
						+ "   Player.Z = __pz;"
						//TODO: Player Dimension
						//+ "   Player.DIM = __pdim;"
						+ "   Player.SunLight = __psunl;"
						+ "   Player.BlockLight = __pblockl;"
						+ "   Player.Light = __pl;"
						//TODO: CanSeeSky, CanRainOn
						//+ "   Player.CanSeeSky = __pseesky;"
						//+ "   Player.CanRainOn = __prainon;"
						+ "   Player.InBoat = __pboat;"
						+ "   Player.Submerged = __psubm;"
						+ "   Player.IsExposed = __pexpo;"
					//WORLD
						+ "   World.Time = __wt;"
						+ "   World.IsRaining = __wrain;"
						+ "   World.MoonPhase = __wmoon;"
						+ "   World.Biome = __wbiome;"
					//SCANNER
						+ "   Scanner.BlockSize = __sblocks;"
						+ "   Scanner.BiomeSize = __sbiomes;"
						+ "   Scanner.AverageSkyLight = __savgskylight;"
						+ "   Scanner.AverageLight = __savglight;"
						+ "   Scanner.AverageTemperature = __savgtemp;"
						+ "   Scanner.AverageHumidity = __savghum;"
						+ "}")
		);
	}
	
	public static void update(MAPlayer maplayer) {
		BlockScanner scanner = maplayer.getScanner();
		MAScriptEngine se = maplayer.getScriptEngine();
		
		Player player = Bukkit.getPlayer(maplayer.getPlayerUUID());
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
			bd.put("__psubm", (headBlock.getType()==Material.WATER || headBlock.getType()==Material.STATIONARY_WATER));
			bd.put("__pexpo", exposed);
		}
		//WORLD
		{
			bd.put("__wt", world.getTime());
			bd.put("__wrain", world.hasStorm());
			bd.put("__wmoon", ((world.getFullTime()/24000) % 8));
			bd.put("__wbiome", world.getBiome(x, z).toString());
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
		
		
		/*
		//PLAYER
		se.evalJS("Player.X = "+x+";"
				+ "Player.Y = "+y+";"
				+ "Player.Z = "+z+";"
		//TODO: Player Dimension
		//		+ "Player.DIM = "+";"
				+ "Player.SunLight = "+block.getLightFromSky()+";"
				+ "Player.BlockLight = "+block.getLightFromBlocks()+";"
				+ "Player.Light = "+block.getLightLevel()+";"
		//TODO: CanSeeSky, CanRainOn
		//		+ "Player.CanSeeSky = "+";"
		//		+ "Player.CanRainOn = "+";"
				+ "Player.InBoat = "+(player.getVehicle() != null && player.getVehicle().getType() == EntityType.BOAT)+";"
				+ "Player.Submerged = "+(headBlock.getType()==Material.WATER || headBlock.getType()==Material.STATIONARY_WATER)+";"
				+ "Player.IsExposed = "+exposed+";"
		//WORLD
		        + "World.Time = "+world.getTime()+";"
				+ "World.IsRaining = "+world.hasStorm()+";"
				+ "World.MoonPhase = "+((world.getFullTime()/24000) % 8)+";"
				+ "World.Biome = \""+world.getBiome(x, z)+"\";"
		//SCANNER
		        + "Scanner.BlockSize = "+scanner.getScanBlockCount()+";"
				+ "Scanner.BiomeSize = "+scanner.getScanBiomeCount()+";"
		        + "Scanner.AverageSkyLight = "+scanner.getAverageSkyLight()+";"
		        + "Scanner.AverageLight = "+scanner.getAverageLight()+";"
		        + "Scanner.AverageTemperature = "+scanner.getAverageTemperature()+";"
		        + "Scanner.AverageHumidity = "+scanner.getAverageHumidity()+";");
		*/
		
		//TODO: Move to precompiled varsetterscript
		//DEFINED BY JSON FILE
		StringBuilder sb = new StringBuilder("");
		for(Variable variable : variables) {
			int count = 0;
			if(variable instanceof BlockCountVariable) {
				for(Material material : ((BlockCountVariable) variable).materials) {
					if(material!=null) count += scanner.getScanBlockData().get(material);
				}
			} else if(variable instanceof BiomeCountVariable) {
				for(Biome biome : ((BiomeCountVariable) variable).biomes) {
					if(biome!=null) count += scanner.getScanBiomeData().get(biome);
				}
			}
			sb.append(variable.name+" = "+count+";");
		}
		se.evalJS(sb.toString());
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
                    if (block.getLightFromSky() > 0)
                        return true;
                }
            }
        }
        return false;
	}
	//private static boolean slowExposedCheck(Location location) {
	//	
	//}
	
	public static void addBlockCountVariable(String name, Material[] materials) {
		BlockCountVariable variable = new BlockCountVariable();
		variable.name = name;
		variable.materials = materials;
		variables.add(variable);
	}
	
	public static void addBiomeCountVariable(String name, Biome[] biomes) {
		BiomeCountVariable variable = new BiomeCountVariable();
		variable.name = name;
		variable.biomes = biomes;
		variables.add(variable);
	}
	
	private static class Variable {
		public String name;
	}
	private static class BlockCountVariable extends Variable {
		public Material[] materials;
	}
	private static class BiomeCountVariable extends Variable {
		public Biome[] biomes;
	}
}
