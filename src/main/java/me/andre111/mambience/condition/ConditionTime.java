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

import me.andre111.mambience.MAPlayer;

public final class ConditionTime extends Condition {
	private final int minTime;
	private final int maxTime;
	
	public ConditionTime(int minTime, int maxTime) {
		this.minTime = minTime;
		this.maxTime = maxTime;
	}

	@Override
	public boolean matches(MAPlayer player) {
		long time = player.getVariables().getTime();
		return minTime <= time && time <= maxTime;
	}
}
