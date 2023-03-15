/*
 * Copyright (c) 2022 Andre Schweiger
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
package me.andre111.mambience.scan;

import java.util.HashMap;
import java.util.Map;

import me.andre111.mambience.accessor.Accessor;

public final class Variables {
	private final Accessor accessor;
	private final BlockScanner scanner;
	
	private final Map<String, Object> values = new HashMap<>();
	private final Map<String, Object> previousValues = new HashMap<>();
	
	public Variables(Accessor a, BlockScanner s) {
		accessor = a;
		scanner = s;
	}
	
	public void update() {
		put("x", accessor.getX());
		put("y", accessor.getY());
		put("z", accessor.getZ());
		
		put("health", accessor.getHealth());
		put("foodLevel", accessor.getFoodLevel());
		
		put("sneaking", accessor.isSneaking());
		put("jumping", accessor.isJumping());
		put("onGround", accessor.isOnGround());
		
		boolean exposed = fastExposedCheck(accessor);
		//TODO: Slower more accurate exposed check
		//if(exposed) exposed = slowExposedCheck(accessor);
		put("exposed", exposed);
		put("underground", !exposed && accessor.getY() < 56);
		put("submerged", accessor.isSubmerged());
		
		put("time", accessor.getDayTime());
		put("raining", accessor.isRaining());
		put("thundering", accessor.isThundering());
		
		put("itemMainHand", accessor.getHeldItem(true));
		put("itemOffHand", accessor.getHeldItem(false));
		
		put("temperature", scanner.getAverageTemperature());
		put("avgLight", scanner.getAverageLight());
		put("avgSkyLight", scanner.getAverageSkyLight());
	}
	
	public Object get(String key) {
		return values.get(key);
	}
	
	public Object getPrevious(String key) {
		return previousValues.get(key);
	}
	
	private void put(String key, Object value) {
		previousValues.put(key, values.put(key, value));
	}
	
	private static boolean fastExposedCheck(Accessor accessor) {
		int x = (int) accessor.getX();
		int y = (int) accessor.getY();
		int z = (int) accessor.getZ();
		
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
	//private static boolean slowExposedCheck(Accessor accessor) {
		//TODO: start from x,y,z (and x,y+1,z) and search horizontally outwards (up to ? blocks) through "open" blocks - then scan upwards for sky access from all found locations
	//}
}
