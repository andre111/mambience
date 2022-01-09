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
package me.andre111.mambience.config;

import java.util.List;

import com.google.gson.JsonObject;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.MAmbience;
import me.andre111.mambience.condition.Condition;
import me.andre111.mambience.effect.Effect;
import me.andre111.mambience.effect.Effects;

public final class EffectLoader {
	public static void reset() {
		Effects.reset();
	}
	
	public static void loadEffect(String id, JsonObject obj) {
		Effects.addEffect(loadEffect(MAmbience.getLogger(), obj));
	}
	
	private static Effect loadEffect(MALogger logger, JsonObject obj) {
		String type = ConfigUtil.getString(obj, "type", "");
		String[] parameters = ConfigUtil.getStringArray(obj, "parameters", new String[] {});
		
		String block = ConfigUtil.getString(obj, "block", "");
		String blockAbove = ConfigUtil.getString(obj, "blockAbove", "");
		String blockBelow = ConfigUtil.getString(obj, "blockBelow", "");
		double chance = ConfigUtil.getDouble(obj, "chance", 1);
		
		List<Condition> conditions = ConfigUtil.loadConditions(logger, obj.get("conditions").getAsJsonArray());
		List<Condition> restrictions = ConfigUtil.loadConditions(logger, obj.get("restrictions").getAsJsonArray());
		
		return new Effect(type, parameters, block, blockAbove, blockBelow, chance, conditions, restrictions);
	}
}
