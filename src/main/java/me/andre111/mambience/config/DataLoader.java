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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.andre111.mambience.MALogger;

public final class DataLoader {
	private static Map<String, List<String>> BIOME_GROUPS;
	
	public static void loadData(MALogger logger, File file) {
		try(CommentSkippingReader reader = new CommentSkippingReader(new BufferedReader(new FileReader(file)))) {
			JsonObject dataElement = JsonParser.parseString(reader.readAllLines("\n")).getAsJsonObject();
			
			loadBiomeGroups(logger, dataElement.get("biomeGroups").getAsJsonArray());
		} catch (Exception e) {
			logger.error("Exception loading data: "+file.getAbsolutePath()+": "+e);
			e.printStackTrace();
		}
	}

	private static void loadBiomeGroups(MALogger logger, JsonArray array) {
		BIOME_GROUPS = new HashMap<>();
		for(int i=0; i<array.size(); i++) {
			JsonObject obj = array.get(i).getAsJsonObject();
			
			String name = obj.get("name").getAsString();
			List<String> biomes = new ArrayList<>();
			JsonArray biomeArray = obj.get("biomes").getAsJsonArray();
			for(int j=0; j<biomeArray.size(); j++) {
				biomes.add(namespaced(biomeArray.get(j).getAsString()));
			}
			
			BIOME_GROUPS.put(name, biomes);
		}
	}
	
	public static List<String> getBiomeGroup(String name) {
		return BIOME_GROUPS.get(name);
	}
	
	public static String namespaced(String name) {
		return name.contains(":") ? name : "minecraft:" + name;
	}
}
