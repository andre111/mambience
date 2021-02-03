/*
 * Copyright (c) 2020 André Schweiger
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.footstep.FSMaterial;
import me.andre111.mambience.footstep.FSSound;

public final class FootstepConfig {
	public static final Map<String, String> BLOCK_MAP = new HashMap<>();
	public static final Map<String, String> ARMOR_MAP = new HashMap<>();
	public static final Map<String, FSMaterial> MATERIALS = new HashMap<>();
	
	public static void loadFootsteps(MALogger logger, File file) {
		try(CommentSkippingReader reader = new CommentSkippingReader(new BufferedReader(new FileReader(file)))) {
			JsonParser parser = new JsonParser();
			JsonObject footstepElement = parser.parse(reader.readAllLines("\n")).getAsJsonObject();
			
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
			logger.error("Exception reading sound config: "+file.getAbsolutePath()+": "+e);
			e.printStackTrace();
		}
	}
	
	private static FSMaterial loadMaterial(JsonObject object) {
		FSSound[] wanderSounds = loadSounds(object.get("wander"));
		FSSound[] walkSounds = loadSounds(object.get("walk"));
		FSSound[] runSounds = loadSounds(object.get("run"));
		FSSound[] jumpSounds = loadSounds(object.get("jump"));
		FSSound[] landSounds = loadSounds(object.get("land"));
		return new FSMaterial(wanderSounds, walkSounds, runSounds, jumpSounds, landSounds);
	}
	
	private static FSSound[] loadSounds(JsonElement element) {
		if(element == null) return new FSSound[] {};
		// shortcut: use just a string for the sound name
		if(element.isJsonPrimitive()) {
			String name = element.getAsString();
			if(!name.contains(":")) name = "mambience:" + name;
			return new FSSound[] { new FSSound(name, 1, 1, 1, 1, 0, 1) };
		}
		// object: single sound object
		if(element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			
			String name = object.get("name").getAsString();
			if(!name.contains(":")) name = "mambience:" + name;
			float volumeMin = object.has("vol_min") ? object.get("vol_min").getAsFloat() / 100 : 1;
			float volumeMax = object.has("vol_max") ? object.get("vol_max").getAsFloat() / 100 : 1;
			float pitchMin = object.has("pitch_min") ? object.get("pitch_min").getAsFloat() / 100 : 1;
			float pitchMax = object.has("pitch_max") ? object.get("pitch_max").getAsFloat() / 100 : 1;
			int delay = object.has("delay") ? object.get("delay").getAsInt() : 0;
			double probability = object.has("probability") ? object.get("probability").getAsDouble() : 1;
			
			return new FSSound[] { new FSSound(name, volumeMin, volumeMax, pitchMin, pitchMax, delay, probability ) };
		}
		// array: list of sounds (with full recursive parsing)
		if(element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			
			List<FSSound> sounds = new ArrayList<>();
			for(int i=0; i<array.size(); i++) {
				for(FSSound sound : loadSounds(array.get(i))) {
					sounds.add(sound);
				}
			}
			return sounds.toArray(new FSSound[0]);
		}
		throw new RuntimeException("Unsupported sound format: "+element);
	}
}
