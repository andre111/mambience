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

public class ConditionVariable extends Condition {
	private final String variable;
	private final boolean previous;
	
	private final double minValue;
	private final double maxValue;
	private final String stringValue;
	
	public ConditionVariable(String variable, boolean previous) {
		this(variable, previous, 0, 0, "");
	}
	public ConditionVariable(String variable, boolean previous, double minValue, double maxValue) {
		this(variable, previous, minValue, maxValue, "");
	}
	public ConditionVariable(String variable, boolean previous, String stringValue) {
		this(variable, previous, 0, 0, stringValue);
	}
	public ConditionVariable(String variable, boolean previous, double minValue, double maxValue, String stringValue) {
		if(variable == null || variable.isBlank()) throw new IllegalArgumentException("Variable cannot be null/blank");
		if(minValue > maxValue) throw new IllegalArgumentException("minValue cannot be larger than maxValue");
		
		this.variable = variable;
		this.previous = previous;
		
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.stringValue = stringValue;
	}

	@Override
	public boolean matches(MAPlayer player) {
		Object value = previous ? player.getVariables().getPrevious(variable) : player.getVariables().get(variable);
		
		if(value instanceof Boolean b) {
			return b;
		} else if(value instanceof Number n) {
			double d = n.doubleValue();
			return minValue <= d && d <= maxValue;
		} else if(value instanceof String s) {
			return s.equals(stringValue);
		} else {
			player.getLogger().error("Accessing unknown/unsupported variable: "+variable+" with value: "+value);
			return false;
		}
	}

}
