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

import java.util.Objects;

import me.andre111.mambience.MAPlayer;

public class ConditionVariableChanged extends Condition {
	private final String variable;
	
	public ConditionVariableChanged(String variable) {
		if(variable == null || variable.isBlank()) throw new IllegalArgumentException("Variable cannot be null/blank");
		
		this.variable = variable;
	}

	@Override
	public boolean matches(MAPlayer player) {
		Object value = player.getVariables().get(variable);
		Object previousValue = player.getVariables().getPrevious(variable);
		
		return !Objects.equals(value, previousValue);
	}

}
