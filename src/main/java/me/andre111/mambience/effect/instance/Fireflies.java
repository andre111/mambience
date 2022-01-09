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
package me.andre111.mambience.effect.instance;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.effect.EffectInstance;

public class Fireflies extends EffectInstance {
	private final double x;
	private final double y;
	private final double z;
	
	private final int range;

	public Fireflies(MAPlayer player, double x, double y, double z, int range) {
		super(player, 10 * 20);
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.range = range;
	}

	@Override
	public void tick() {
		super.tick();
		
		if(Math.random() < 1/40.0) {
			getPlayer().getAccessor().addParticle("minecraft:end_rod", "", 
					x+(Math.random()*range+2+1)-range, y+Math.random()*range, z+(Math.random()*range+2+1)-range, 
					0.5*(Math.random() - 0.5), 0.2*(Math.random() - 0.5), 0.5*(Math.random() - 0.5));
		}
	}
}
