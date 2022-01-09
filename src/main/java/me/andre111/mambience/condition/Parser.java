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
package me.andre111.mambience.condition;

import java.util.Collections;

import me.andre111.mambience.config.DataLoader;

public final class Parser {
	private static final Condition TIME_MORNING = new ConditionTime(0, 2000);
	private static final Condition TIME_DAY = new ConditionTime(2000, 12000);
	private static final Condition TIME_EVENING = new ConditionTime(12000, 14000);
	private static final Condition TIME_NIGHT = new ConditionTime(14000, 24000);
	
	private static final Condition EXPOSED = new ConditionExposed();
	private static final Condition SUBMERGED = new ConditionSubmerged();
	private static final Condition UNDERGROUND = new ConditionUnderground();

	private static final Condition RAINING = new ConditionRaining();
	private static final Condition THUNDERING = new ConditionThundering();
	
	public static Condition parse(String name, String stringValue, float floatValue) {
		switch(name) {
		case "TIME":
			String[] timeSplit = stringValue.split("\\.\\.");
			int minTime = Integer.parseInt(timeSplit[0]);
			int maxTime = Integer.parseInt(timeSplit[timeSplit.length > 1 ? 1 : 0]);
			return new ConditionTime(minTime, maxTime);
		case "TIME_MORNING":
			return TIME_MORNING;
		case "TIME_DAY":
			return TIME_DAY;
		case "TIME_EVENING":
			return TIME_EVENING;
		case "TIME_NIGHT":
			return TIME_NIGHT;
			
		case "BIOME":
			return new ConditionBiomes(Collections.singletonList(DataLoader.namespaced(stringValue)), floatValue);
		case "BIOME_GROUP":
			return new ConditionBiomes(DataLoader.getBiomeGroup(stringValue), floatValue);
			
		case "BLOCK":
			return new ConditionBlocks(DataLoader.namespaced(stringValue), floatValue);
			
		case "HEIGHT":
			float minHeight = Float.parseFloat(stringValue);
			float maxHeight = floatValue;
			return new ConditionHeight(minHeight, maxHeight);
		case "EXPOSED":
			return EXPOSED;
		case "SUBMERGED":
			return SUBMERGED;
		case "UNDERGROUND":
			return UNDERGROUND;
			
		case "RAINING":
			return RAINING;
		case "THUNDERING":
			return THUNDERING;
		}
		return null;
	}
}
