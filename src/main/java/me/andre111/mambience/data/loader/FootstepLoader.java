/*
 * Copyright (c) 2024 Andre Schweiger
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
package me.andre111.mambience.data.loader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.andre111.mambience.movement.FSMaterial;

public final class FootstepLoader {
	public static final Map<String, List<FSMaterial>> BLOCK_MAP = new HashMap<>();
	public static final Map<String, List<FSMaterial>> ARMOR_MAP = new HashMap<>();
	
	public static void reset() {
		BLOCK_MAP.clear();
		ARMOR_MAP.clear();
	}
	
	public static void loadFootsteps(JsonObject obj) {
		loadToMap(BLOCK_MAP, obj.get("blocks").getAsJsonObject());
		loadToMap(ARMOR_MAP, obj.get("armor").getAsJsonObject());
	}
	
	private static void loadToMap(Map<String, List<FSMaterial>> map, JsonObject obj) {
		for(Map.Entry<String, JsonElement> e : obj.entrySet()) {
			map.put(e.getKey(), Arrays.stream(e.getValue().getAsString().split(",")).map(id -> MaterialLoader.getMaterial(id)).toList());
		}
	}

	public static void addBlock(String block, String type) {
		BLOCK_MAP.put(block, Arrays.stream(type.split(",")).map(id -> MaterialLoader.getMaterial(id)).toList());
	}
}
