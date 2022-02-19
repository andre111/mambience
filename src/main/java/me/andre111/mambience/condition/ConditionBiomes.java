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
package me.andre111.mambience.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.andre111.mambience.MAPlayer;

public final class ConditionBiomes extends Condition {
	private final String biomeOrTag;
	private final float minPercentage;
	
	private List<String> cachedBiomes;
	
	public ConditionBiomes(String biomeOrTag, float minPercentage) {
		if(biomeOrTag == null) throw new IllegalArgumentException("Biome / Biometag cannot be null");
		if(minPercentage < 0 || minPercentage > 1) throw new IllegalArgumentException("Minimum percentage is outside valid range [0,1]");
		
		this.biomeOrTag = biomeOrTag;
		this.minPercentage = minPercentage;
	}

	@Override
	public boolean matches(MAPlayer player) {
		// cache actual biome names
		if(cachedBiomes == null) {
			cachedBiomes = new ArrayList<>();
			if(biomeOrTag.startsWith("#")) {
				cachedBiomes.addAll(player.getAccessor().getBiomeTag(biomeOrTag.substring(1)));
			} else {
				cachedBiomes.add(biomeOrTag);
			}
		}
		
		// get data
		Map<String, Integer> scanData = player.getScanner().getScanBiomeData();
		
		int count = 0;
		if(scanData != null) {
			for(String biome : cachedBiomes) {
				count += scanData.getOrDefault(biome, 0);
			}
		}
		
		float percentage = count / (float) player.getScanner().getScanBiomeCount();
		return percentage >= minPercentage;
	}
}
