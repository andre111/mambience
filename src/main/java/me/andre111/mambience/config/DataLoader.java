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
	private static Map<String, List<String>> BLOCK_TAGS;
	
	public static void loadData(MALogger logger, File file) {
		try(CommentSkippingReader reader = new CommentSkippingReader(new BufferedReader(new FileReader(file)))) {
			JsonObject dataElement = JsonParser.parseString(reader.readAllLines("\n")).getAsJsonObject();
			
			loadBiomeGroups(logger, dataElement.get("biomeGroups").getAsJsonArray());
			loadBlockTags(logger, dataElement.get("tags").getAsJsonArray());
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

	private static void loadBlockTags(MALogger logger, JsonArray array) {
		BLOCK_TAGS = new HashMap<>();
		//TODO: load vanilla tags (how, they are per world?)
		
		// load custom tags
		for(int i=0; i<array.size(); i++) {
			JsonObject obj = array.get(i).getAsJsonObject();
			if(!obj.get("type").getAsString().equals("BLOCKS")) continue;
			if(obj.has("dpOnly") && obj.get("dpOnly").getAsBoolean()) continue;
			
			String name = "#andre111:mambience/" + obj.get("name").getAsString();
			List<String> blocks = new ArrayList<>();
			JsonArray blocksArray = obj.get("values").getAsJsonArray();
			for(int j=0; j<blocksArray.size(); j++) {
				String value = blocksArray.get(j).getAsString();
				if(value.startsWith("#")) {
					// replace vanilla tags with their mirrors
					if(value.startsWith("#minecraft:")) value = "#andre111:mambience/" + value.substring("#minecraft:".length()); //TODO: remove once vanilla tags can be used
					
					if(!BLOCK_TAGS.containsKey(value)) throw new RuntimeException("Unknown tag "+value+" when loading tag "+name);
					
					blocks.addAll(BLOCK_TAGS.get(value));
				} else {
					blocks.add(namespaced(value));
				}
			}
			
			BLOCK_TAGS.put(name, blocks);
		}
	}
	
	public static List<String> getBiomeGroup(String name) {
		return BIOME_GROUPS.get(name);
	}
	
	public static List<String> getBlockTag(String name) {
		return BLOCK_TAGS.get(name);
	}
	
	public static String namespaced(String name) {
		return name.contains(":") ? name : "minecraft:" + name;
	}
}
