/*
 * Copyright (c) 2021 Andre Schweiger
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
package me.andre111.mambience.effect;

import java.util.List;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.condition.Condition;
import me.andre111.mambience.effect.instance.DustWind;
import me.andre111.mambience.effect.instance.FallingLeaves;
import me.andre111.mambience.effect.instance.Fireflies;
import me.andre111.mambience.effect.instance.FlameJet;

public class Effect {
	private final String type;
	private final String[] parameters;
	
	private final String block;
	private final String blockAbove;
	private final String blockBelow;
	private final double chance;
	
	private final List<Condition> conditions;
	private final List<Condition> restrictions;
	
	public Effect(String type, String[] parameters, String block, String blockAbove, String blockBelow, double chance, List<Condition> conditions, List<Condition> restrictions) {
		this.type = type;
		this.parameters = parameters;
		
		this.block = block;
		this.blockAbove = blockAbove;
		this.blockBelow = blockBelow;
		this.chance = chance;
		
		this.conditions = conditions;
		this.restrictions = restrictions;
	}

	public void update(MAPlayer maplayer, String block, int x, int y, int z) {
		if(conditionsMet(maplayer, block, x, y, z)) {
			switch(type) {
			case "fire_jet":
				// generate height and clip by available space
				int height = (int) (Math.random() * 3 + 1);
				for(int i=1; i<=height; i++) {
					if(!maplayer.getAccessor().getBlock(x, y+i, z).equals("minecraft:air")) {
						height = i-1;
						break;
					}
				}
				
				if(height > 0) Effects.addInstance(new FlameJet(maplayer, height, x+Math.random(), y+0.5, z+Math.random()));
				break;
			case "fireflies":
				Effects.addInstance(new Fireflies(maplayer, x, y, z, 5));
				break;
			case "dust_wind":
				Effects.addInstance(new DustWind(maplayer, parameters[0], DustWind.WIND_X, DustWind.WIND_Z, x, y, z));
				break;
			case "falling_leaves":
				Effects.addInstance(new FallingLeaves(maplayer, parameters[0], x, y, z));
				break;
			}
		}
	}

	private boolean conditionsMet(MAPlayer maplayer, String block, int x, int y, int z) {
		if(chance < 1 && Math.random() > chance) return false;
		if(!block.equals(this.block)) return false;
		if(!blockAbove.isEmpty() && !maplayer.getAccessor().getBlock(x, y+1, z).equals(blockAbove)) return false;
		if(!blockBelow.isEmpty() && !maplayer.getAccessor().getBlock(x, y-1, z).equals(blockBelow)) return false;
		
		for(Condition condition : conditions) {
			if(!condition.matches(maplayer)) return false;
		}
		for(Condition restriction : restrictions) {
			if(restriction.matches(maplayer)) return false;
		}
		
		return true;
	}
}
