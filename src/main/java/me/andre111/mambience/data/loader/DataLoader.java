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
package me.andre111.mambience.data.loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.andre111.mambience.MAmbience;
import me.andre111.mambience.data.Data;
import me.andre111.mambience.data.DataLocator;

public class DataLoader {
	public static void reload(DataLocator locator) {
		// reset old data
		MaterialLoader.reset();
		EffectLoader.reset();
		EventLoader.reset();
		
		// load new data
		loadEntries(locator, "ma_materials", MaterialLoader::loadMaterial);
		loadEntries(locator, "ma_sounds", EventLoader::loadEvent);
		loadEntries(locator, "ma_effects", EffectLoader::loadEffect);
		loadReplaceable(locator, "mambience:ma_footsteps.json", FootstepLoader::reset, FootstepLoader::loadFootsteps);
	}
	
	private static void loadEntries(DataLocator locator, String startingPath, BiConsumer<String, JsonObject> callback) {
		Set<String> loaded = new HashSet<>();
		for(String id : locator.findData(startingPath, path -> path.endsWith(".json"))) {
			// avoid loading duplicates (findResources does not guarantee non duplicate entries)
			if(loaded.contains(id)) continue;
			loaded.add(id);
			
			// actual loading
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(locator.getData(id).openInputStream()))) {
				JsonElement element = JsonParser.parseReader(reader);
				if(element instanceof JsonObject object) {
					String actualID = id.substring(0, id.length()-5).replace(startingPath+"/", "");
					
					MAmbience.getLogger().log("Loading " + actualID + " - " + id);
					
					callback.accept(actualID.toString(), object);
				} else {
					throw new RuntimeException("Root is not a json object");
				}
			} catch(Exception e) {
				MAmbience.getLogger().error("Exception while loading json " + id.toString() + ": " + e.getMessage());
			}
		}
	}
	
	private static void loadReplaceable(DataLocator locator, String id, Runnable resetCallback, Consumer<JsonObject> callback) {
		try {
			resetCallback.run();
			for(Data data : locator.getAllData(id)) {
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(data.openInputStream()))) {
					JsonElement element = JsonParser.parseReader(reader);
					if(element instanceof JsonObject object) {
						// check for replace setting and call reset of required
						if(object.has("replace") && object.get("replace").isJsonPrimitive() && object.get("replace").getAsBoolean()) {
							resetCallback.run();
						}
						
						// load data
						callback.accept(object);
					} else {
						throw new RuntimeException("Root is not a json object");
					}
				} catch(Exception e) {
					MAmbience.getLogger().error("Exception while loading json " + id.toString() + ": " + e.getMessage());
				}
			}
		} catch (Exception e) {
			MAmbience.getLogger().error("Exception while loading json " + id.toString() + ": " + e.getMessage());
		}
	}
}
