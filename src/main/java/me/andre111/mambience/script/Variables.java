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
package me.andre111.mambience.script;

import java.util.ArrayList;

import javax.script.Bindings;

import me.andre111.mambience.player.Accessor;
import me.andre111.mambience.player.MAPlayer;
import me.andre111.mambience.scan.BlockScanner;

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
						//+ "   Player.InBoat = __pboat;"
						+ "   Player.Submerged = __psubm;"
						+ "   Player.IsExposed = __pexpo;"
						+ "   Player.Health = __phealth;"
						+ "   Player.Food = __pfood;"
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
		Accessor accessor = maplayer.getAccessor();
		BlockScanner scanner = maplayer.getScanner();
		MAScriptEngine se = maplayer.getScriptEngine();
		
		if(!accessor.updatePlayerInstance()) return; // fixes server crashes, TODO: but this should never even happen
		
		int x = accessor.getX();
		int y = accessor.getY();
		int z = accessor.getZ();
		
		boolean exposed = fastExposedCheck(accessor, x, y, z);
		//TODO: Slower more accurate exposed check
		//if(exposed) exposed = slowExposedCheck(accessor, x, y, z);
		
		Bindings bd = se.getEngineScopeBindings();
		//PLAYER
		{
			bd.put("__px", x);
			bd.put("__py", y);
			bd.put("__pz", z);
			//TODO: Player Dimension
			//bd.put("__pdim", );
			bd.put("__psunl", accessor.getSkyLight(x, y, z));
			bd.put("__pblockl", accessor.getBlockLight(x, y, z));
			bd.put("__pl", accessor.getLight(x, y, z));
			//TODO: CanSeeSky, CanRainOn
			//bd.put("__pseesky", );
			//bd.put("__prainon", );
			//bd.put("__pboat", );
			bd.put("__psubm", accessor.isSubmerged());
			bd.put("__pexpo", exposed);
			bd.put("__phealth", accessor.getHealth());
			bd.put("__pfood", accessor.getFoodLevel());
		}
		//WORLD
		{
			bd.put("__wt", accessor.getDayTime());
			bd.put("__wrain", accessor.isRaining());
			bd.put("__wmoon", (accessor.getFullTime()/24000) % 8);
			bd.put("__wbiome", accessor.getBiome(x, y, z));
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
	
	private static boolean fastExposedCheck(Accessor accessor, int x, int y, int z) {
		int mx = x + 1;
        int my = y + 1;
        int mz = z + 1;
        
        for (int cx = mx - 2; cx <= mx; cx++) {
            for (int cy = my - 2; cy <= my; cy++) {
                for (int cz = mz - 2; cz <= mz; cz++) {
                    if (!accessor.getBlock(cx, cy, cz).equals("minecraft:air"))
                        continue;
                    if (accessor.getSkyLight(cx, cy, cz) > 4) // increased from 0
                        return true;
                }
            }
        }
        return false;
	}
	//private static boolean slowExposedCheck(Accessor accessor, int x, int y, int z) {
	//	
	//}
	
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
}
