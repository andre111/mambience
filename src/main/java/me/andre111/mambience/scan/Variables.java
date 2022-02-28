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

import me.andre111.mambience.accessor.Accessor;

public final class Variables {
	private Accessor accessor;
	
	private int x;
	private int y;
	private int z;
	
	private boolean exposed;
	private boolean submerged;
	
	private long time;
	private boolean raining;
	private boolean thundering;
	
	private String itemMainHand;
	private String itemOffHand;
	
	public Variables(Accessor a) {
		accessor = a;
	}
	
	public void update() {
		x = (int) accessor.getX();
		y = (int) accessor.getY();
		z = (int) accessor.getZ();
		
		exposed = fastExposedCheck(accessor, x, y, z);
		//TODO: Slower more accurate exposed check
		//if(exposed) exposed = slowExposedCheck(accessor, x, y, z);
		
		submerged = accessor.isSubmerged();
		
		time = accessor.getDayTime();
		raining = accessor.isRaining();
		thundering = accessor.isThundering();
		
		itemMainHand = accessor.getHeldItem(true);
		itemOffHand = accessor.getHeldItem(false);
	}
	
	public int getHeight() {
		return y;
	}
	
	public boolean isExposed() {
		return exposed;
	}
	public boolean isSubmerged() {
		return submerged;
	}
	
	public long getTime() {
		return time;
	}
	public boolean isRaining() {
		return raining;
	}
	public boolean isThundering() {
		return thundering;
	}
	
	public String getItemMainHand() {
		return itemMainHand;
	}
	public String getItemOffHand() {
		return itemOffHand;
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
		//TODO: start from x,y,z (and x,y+1,z) and search horizontally outwards (up to ? blocks) through "open" blocks - then scan upwards for sky access from all found locations
	//}
}
