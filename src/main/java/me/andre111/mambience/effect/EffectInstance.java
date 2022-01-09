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
package me.andre111.mambience.effect;

import me.andre111.mambience.MAPlayer;

public class EffectInstance {
	private MAPlayer player;
	private int life;
	
	public EffectInstance(MAPlayer player, int life) {
		this.player = player;
		this.life = life;
	}
	
	public void tick() {
		this.life--;
	}
	
	public boolean isAlive() {
		return this.life > 0;
	}
	
	public MAPlayer getPlayer() {
		return player;
	}
	
	public int getLife() {
		return life;
	}
}
