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
package me.andre111.mambience.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.andre111.mambience.MAPlayer;

public final class ConditionBiomes extends Condition {
	private final List<String> biomes;
	private final float minPercentage;
	
	public ConditionBiomes(List<String> biomes, float minPercentage) {
		this.biomes = new ArrayList<>(biomes);
		this.minPercentage = minPercentage;
	}

	@Override
	public boolean matches(MAPlayer player) {
		Map<String, Integer> scanData = player.getScanner().getScanBiomeData();
		
		int count = 0;
		for(String biome : biomes) {
			count += scanData != null && biome != null && scanData.containsKey(biome) ? scanData.get(biome) : 0;
		}
		
		float percentage = count / (float) player.getScanner().getScanBiomeCount();
		return percentage >= minPercentage;
	}
}
