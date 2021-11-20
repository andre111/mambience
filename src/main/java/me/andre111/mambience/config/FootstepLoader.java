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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.footstep.FSMaterial;
import me.andre111.mambience.sound.Sound;

public final class FootstepLoader {
	public static final Map<String, String> BLOCK_MAP = new HashMap<>();
	public static final Map<String, String> ARMOR_MAP = new HashMap<>();
	public static final Map<String, FSMaterial> MATERIALS = new HashMap<>();
	
	public static void loadFootsteps(MALogger logger, File file) {
		try(CommentSkippingReader reader = new CommentSkippingReader(new BufferedReader(new FileReader(file)))) {
			JsonObject footstepElement = JsonParser.parseString(reader.readAllLines("\n")).getAsJsonObject();
			
			BLOCK_MAP.clear();
			ARMOR_MAP.clear();
			MATERIALS.clear();
			
			// load block map
			JsonObject blockMapElement = footstepElement.get("blocks").getAsJsonObject();
			for(Map.Entry<String, JsonElement> e : blockMapElement.entrySet()) {
				BLOCK_MAP.put(e.getKey(), e.getValue().getAsString());
			}
			
			// load block map
			JsonObject armorMapElement = footstepElement.get("armor").getAsJsonObject();
			for(Map.Entry<String, JsonElement> e : armorMapElement.entrySet()) {
				ARMOR_MAP.put(e.getKey(), e.getValue().getAsString());
			}
			
			// load materials
			JsonObject materialsElement = footstepElement.get("materials").getAsJsonObject();
			for(Map.Entry<String, JsonElement> e : materialsElement.entrySet()) {
				MATERIALS.put(e.getKey(), loadMaterial(e.getValue().getAsJsonObject()));
			}
		} catch (Exception e) {
			logger.error("Exception loading footsteps: "+file.getAbsolutePath()+": "+e);
			e.printStackTrace();
		}
	}
	
	private static FSMaterial loadMaterial(JsonObject object) {
		Sound[] wanderSounds = ConfigUtil.loadSounds(object.get("wander"), Config.footsteps().getVolume());
		Sound[] walkSounds = ConfigUtil.loadSounds(object.get("walk"), Config.footsteps().getVolume());
		Sound[] runSounds = ConfigUtil.loadSounds(object.get("run"), Config.footsteps().getVolume());
		Sound[] jumpSounds = ConfigUtil.loadSounds(object.get("jump"), Config.footsteps().getVolume());
		Sound[] landSounds = ConfigUtil.loadSounds(object.get("land"), Config.footsteps().getVolume());
		return new FSMaterial(wanderSounds, walkSounds, runSounds, jumpSounds, landSounds);
	}
}
