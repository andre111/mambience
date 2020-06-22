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
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.condition.Condition;
import me.andre111.mambience.condition.Parser;
import me.andre111.mambience.sound.Sound;
import me.andre111.mambience.sound.Sounds;

public final class SoundConfig {
	public static void loadSounds(MALogger logger, File file) {
		try(CommentSkippingReader reader = new CommentSkippingReader(new BufferedReader(new FileReader(file)))) {
			JsonParser parser = new JsonParser();
			JsonArray soundElement = parser.parse(reader.readAllLines("\n")).getAsJsonArray();
			
			Sounds.reset();
			for(int i=0; i<soundElement.size(); i++) {
				Sound sound = loadSound(logger, i, soundElement.get(i).getAsJsonObject());
				Sounds.addSound(sound);
			}
		} catch (Exception e) {
			logger.error("Exception reading sound config: "+file.getAbsolutePath()+": "+e);
			e.printStackTrace();
		}
	}
	
	private static Sound loadSound(MALogger logger, int index, JsonObject obj) {
		String id = Integer.toString(index);
		String sound = getString(obj, "sound", "");
		float volume = getFloat(obj, "volume", 1);
		float pitch = getFloat(obj, "pitch", 1);
		List<Condition> conditions = loadConditions(logger, obj.get("conditions").getAsJsonArray());
		List<Condition> restrictions = loadConditions(logger, obj.get("restrictions").getAsJsonArray());
		int cooldownMin = getInt(obj, "cooldownMin", 1);
		int cooldownMax = getInt(obj, "cooldownMax", 1);
		
		return new Sound(id, sound, volume, pitch, conditions, restrictions, cooldownMin, cooldownMax);
	}
	
	private static List<Condition> loadConditions(MALogger logger, JsonArray array) {
		List<Condition> conditions = new ArrayList<>();
		for(int i=0; i<array.size(); i++) {
			//TODO: remove: ignore toggles
			if(array.get(i).getAsJsonObject().get("condition").getAsString().equals("TOGGLE")) continue;
			
			Condition condition = loadCondition(logger, array.get(i).getAsJsonObject());
			if(condition != null) {
				conditions.add(condition);
			} else {
				logger.log("Warning: Ignored unknown condition: "+array.get(i));
			}
		}
		return conditions;
	}
	
	private static Condition loadCondition(MALogger logger, JsonObject obj) {
		String name = getString(obj, "condition", "");
		String stringValue = getString(obj, "stringValue", "");
		float floatValue = getFloat(obj, "floatValue", 0);
		return Parser.parse(name, stringValue, floatValue);
	}
	
	private static String getString(JsonObject obj, String memberName, String defaultValue) {
		return obj.has(memberName) ? obj.get(memberName).getAsString() : defaultValue;
	}
	private static float getFloat(JsonObject obj, String memberName, float defaultValue) {
		return obj.has(memberName) ? obj.get(memberName).getAsFloat() : defaultValue;
	}
	private static int getInt(JsonObject obj, String memberName, int defaultValue) {
		return obj.has(memberName) ? obj.get(memberName).getAsInt() : defaultValue;
	}
}
