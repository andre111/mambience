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

public final class ConditionBlocks extends Condition {
	private final List<String> blocks;
	private final float minPercentage;
	
	public ConditionBlocks(List<String> blocks, float minPercentage) {
		this.blocks = new ArrayList<>(blocks);
		this.minPercentage = minPercentage;
	}

	@Override
	public boolean matches(MAPlayer player) {
		Map<String, Integer> scanData = player.getScanner().getScanBlockData();
		
		int count = 0;
		for(String block : blocks) {
			count += scanData != null && block != null && scanData.containsKey(block) ? scanData.get(block) : 0;
		}
		
		float percentage = count / (float) player.getScanner().getScanBlockCount();
		return percentage >= minPercentage;
	}
}
