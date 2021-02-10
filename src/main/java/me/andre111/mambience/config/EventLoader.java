/*
 * Copyright (c) 2021 André Schweiger
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.ambient.AmbientEvent;
import me.andre111.mambience.condition.Condition;
import me.andre111.mambience.sound.Sound;

public final class EventLoader {
	public static final Set<AmbientEvent> EVENTS = new HashSet<>();
	
	public static void loadEvents(MALogger logger, File file) {
		try(CommentSkippingReader reader = new CommentSkippingReader(new BufferedReader(new FileReader(file)))) {
			JsonParser parser = new JsonParser();
			JsonArray soundElement = parser.parse(reader.readAllLines("\n")).getAsJsonArray();
			
			EVENTS.clear();
			for(int i=0; i<soundElement.size(); i++) {
				AmbientEvent sound = loadEvent(logger, i, soundElement.get(i).getAsJsonObject());
				EVENTS.add(sound);
			}
		} catch (Exception e) {
			logger.error("Exception loading sounds: "+file.getAbsolutePath()+": "+e);
			e.printStackTrace();
		}
	}
	
	private static AmbientEvent loadEvent(MALogger logger, int index, JsonObject obj) {
		String id = Integer.toString(index);
		Sound[] sounds = ConfigUtil.loadSounds(obj.get("sound"), Config.ambientEvents().getVolume());
		List<Condition> conditions = ConfigUtil.loadConditions(logger, obj.get("conditions").getAsJsonArray());
		List<Condition> restrictions = ConfigUtil.loadConditions(logger, obj.get("restrictions").getAsJsonArray());
		int cooldownMin = ConfigUtil.getInt(obj, "cooldownMin", 1);
		int cooldownMax = ConfigUtil.getInt(obj, "cooldownMax", 1);
		
		return new AmbientEvent(id, sounds, conditions, restrictions, cooldownMin, cooldownMax);
	}
}
