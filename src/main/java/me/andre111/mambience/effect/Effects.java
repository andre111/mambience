/*
 * Copyright (c) 2021 André Schweiger
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.accessor.Accessor;
import me.andre111.mambience.config.Config;

public class Effects {
	private static Random random = new Random();
	private static Set<Effect> effects = new HashSet<>();
	private static List<EffectInstance> instances = new ArrayList<>();

	public static void reset() {
		effects.clear();
	}
	
	public static void addEffect(Effect effect) {
		effects.add(effect);
	}
	
	// call to tick effect instances
	public static void tick() {
		Iterator<EffectInstance> iter = instances.iterator();
		while(iter.hasNext()) {
			EffectInstance instance = iter.next();
			instance.tick();
			
			if(!instance.isAlive()) {
				iter.remove();
			}
		}
	}
	
	// called to check for new effects
	public static void update(MAPlayer player) {
		Accessor accessor = player.getAccessor();
		
		// DO ACTUAL BLOCK TESTING
		int bx = (int) accessor.getX();
		int by = (int) accessor.getY();
		int bz = (int) accessor.getZ();
		for(int i=0; i<Config.effects().getRandomTicks(); i++) {
			int ox = random.nextInt(Config.effects().getSizeX() + 1) - Config.effects().getSizeX()/2;
			int oy = random.nextInt(Config.effects().getSizeY() + 1) - Config.effects().getSizeY()/2;
			int oz = random.nextInt(Config.effects().getSizeZ() + 1) - Config.effects().getSizeZ()/2;
			
			String block = player.getAccessor().getBlock(bx+ox, by+oy, bz+oz);
			
			for(Effect effect : effects) {
				effect.update(player, block, bx+ox, by+oy, bz+oz);
			}
		}
	}
	
	public static void addInstance(EffectInstance instance) {
		instances.add(instance);
	}
}
