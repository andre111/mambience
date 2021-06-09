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
package me.andre111.mambience.effect.instance;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.effect.EffectInstance;

public class FlameJet extends EffectInstance {
	private final String type;
	private final int strength;
	private final double x;
	private final double y;
	private final double z;
	
	private boolean firstTick;

	public FlameJet(MAPlayer player, int strength, double x, double y, double z) {
		super(player, (int) (Math.random() * strength + 2) * 20);
		
		this.type = Math.random() < 0.1 ? "minecraft:lava" : "minecraft:flame";
		this.strength = strength;
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.firstTick = true;
	}

	@Override
	public void tick() {
		super.tick();
		
		if(this.firstTick) {
			this.firstTick = false;
			getPlayer().getAccessor().playSound("minecraft:block.fire.ambient", x, y, z, 1, 1);
		}
		
		if(this.getLife() % 3 == 0) {
			double velocityY = strength/12.0;
			getPlayer().getAccessor().addParticle(type, "", x, y, z, 0, velocityY, 0);
			getPlayer().getAccessor().addParticle("minecraft:smoke", "", x, y, z, 0, velocityY, 0);
			

			getPlayer().getAccessor().addParticle(type, "", x-0.25, y+Math.random()*0.5, z, +0.02/strength, velocityY, 0);
			getPlayer().getAccessor().addParticle(type, "", x+0.25, y+Math.random()*0.5, z, -0.02/strength, velocityY, 0);
			getPlayer().getAccessor().addParticle(type, "", x, y+Math.random()*0.5, z-0.25, 0, velocityY, +0.02/strength);
			getPlayer().getAccessor().addParticle(type, "", x, y+Math.random()*0.5, z+0.25, 0, velocityY, -0.02/strength);
		}
	}
}
