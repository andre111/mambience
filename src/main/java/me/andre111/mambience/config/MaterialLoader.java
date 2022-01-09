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
package me.andre111.mambience.config;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import me.andre111.mambience.footstep.FSMaterial;
import me.andre111.mambience.sound.Sound;

public final class MaterialLoader {
	private static final Map<String, FSMaterial> MATERIALS = new HashMap<>();
	
	public static void reset() {
		MATERIALS.clear();
	}
	
	public static void loadMaterial(String id, JsonObject obj) {
		MATERIALS.put(id, loadMaterial(obj));
	}
	
	private static FSMaterial loadMaterial(JsonObject obj) {
		Sound[] wanderSounds = ConfigUtil.loadSounds(obj.get("wander"), Config.footsteps().getVolume());
		Sound[] walkSounds = ConfigUtil.loadSounds(obj.get("walk"), Config.footsteps().getVolume());
		Sound[] runSounds = ConfigUtil.loadSounds(obj.get("run"), Config.footsteps().getVolume());
		Sound[] jumpSounds = ConfigUtil.loadSounds(obj.get("jump"), Config.footsteps().getVolume());
		Sound[] landSounds = ConfigUtil.loadSounds(obj.get("land"), Config.footsteps().getVolume());
		return new FSMaterial(wanderSounds, walkSounds, runSounds, jumpSounds, landSounds);
	}
	
	public static FSMaterial getMaterial(String id) {
		if(!id.contains(":")) id = "mambience:" + id;
		
		if(MATERIALS.containsKey(id)) {
			return MATERIALS.get(id);
		} else {
			return MATERIALS.get("mambience:none");
		}
	}
}
