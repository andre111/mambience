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

import java.util.Map;

import me.andre111.mambience.MAPlayer;

public final class ConditionBlocks extends Condition {
	private final String blockOrTag;
	private final float minPercentage;
	
	public ConditionBlocks(String blockOrTag, float minPercentage) {
		this.blockOrTag = blockOrTag;
		this.minPercentage = minPercentage;
	}

	@Override
	public boolean matches(MAPlayer player) {
		Map<String, Integer> scanData = player.getScanner().getScanBlockData();
		
		int count = 0;
		if(scanData != null) {
			if(blockOrTag != null && blockOrTag.startsWith("#")) {
				for(String block : player.getAccessor().getBlockTag(blockOrTag.substring(1))) {
					count += scanData.containsKey(block) ? scanData.get(block) : 0;
				}
			} else {
				count += blockOrTag != null && scanData.containsKey(blockOrTag) ? scanData.get(blockOrTag) : 0;
			}
		}
		
		float percentage = count / (float) player.getScanner().getScanBlockCount();
		return percentage >= minPercentage;
	}
}
