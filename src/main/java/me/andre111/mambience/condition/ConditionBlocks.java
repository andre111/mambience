/*
 * Copyright (c) 2023 Andre Schweiger
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

public final class ConditionBlocks extends Condition {
	private final String blockOrTag;
	private final float minPercentage;
	
	private List<String> cachedBlocks;
	
	public ConditionBlocks(String blockOrTag, float minPercentage) {
		if(blockOrTag == null) throw new IllegalArgumentException("Block / Blocktag cannot be null");
		if(minPercentage < 0 || minPercentage > 1) throw new IllegalArgumentException("Minimum percentage is outside valid range [0,1]");
		
		this.blockOrTag = blockOrTag;
		this.minPercentage = minPercentage;
	}

	@Override
	public boolean matches(MAPlayer player) {
		// cache actual block names
		if(cachedBlocks == null) {
			cachedBlocks = new ArrayList<>();
			if(blockOrTag.startsWith("#")) {
				cachedBlocks.addAll(player.getAccessor().getBlockTag(blockOrTag.substring(1)));
			} else {
				cachedBlocks.add(blockOrTag);
			}
		}
		
		// get data
		Map<String, Integer> scanData = player.getScanner().getScanBlockData();
		
		int count = 0;
		if(scanData != null) {
			for(String block : cachedBlocks) {
				count += scanData.getOrDefault(block, 0);
			}
		}
		
		float percentage = count / (float) player.getScanner().getScanBlockCount();
		return percentage >= minPercentage;
	}
}
