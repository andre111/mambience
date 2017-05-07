package me.andre111.mambience.script;

import java.util.ArrayList;

import me.andre111.mambience.player.MAPlayer;
import me.andre111.mambience.scan.BlockScanner;

public abstract class Variables {
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
		
		maplayer.getVariables().updateVariables(maplayer);
		
		//TODO: Move to precompiled varsetterscript
		//DEFINED BY JSON FILE
		StringBuilder sb = new StringBuilder("");
		for(Variable variable : variables) {
			int count = 0;
			if(variable instanceof BlockCountVariable) {
				for(String blocks : ((BlockCountVariable) variable).blocks) {
					if(blocks!=null && scanner.getScanBlockData().get(blocks)!=null) count += scanner.getScanBlockData().get(blocks);
				}
			} else if(variable instanceof BiomeCountVariable) {
				for(String biome : ((BiomeCountVariable) variable).biomes) {
					if(biome!=null && scanner.getScanBiomeData().get(biome)!=null) count += scanner.getScanBiomeData().get(biome);
				}
			}
			sb.append(variable.name+" = "+count+";");
		}
		se.evalJS(sb.toString());
	}
	
	public static void addBlockCountVariable(String name, String[] blocks) {
		BlockCountVariable variable = new BlockCountVariable();
		variable.name = name;
		variable.blocks = blocks;
		variables.add(variable);
	}
	
	public static void addBiomeCountVariable(String name, String[] biomes) {
		BiomeCountVariable variable = new BiomeCountVariable();
		variable.name = name;
		variable.biomes = biomes;
		variables.add(variable);
	}
	
	private static class Variable {
		public String name;
	}
	private static class BlockCountVariable extends Variable {
		public String[] blocks;
	}
	private static class BiomeCountVariable extends Variable {
		public String[] biomes;
	}
	
	public abstract void updateVariables(MAPlayer maplayer);
}
