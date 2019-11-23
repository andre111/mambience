/*
 * Copyright (c) 2019 André Schweiger
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.sound.Soundscape;
import me.andre111.mambience.sound.Soundscape.SoundInfo;
import me.andre111.mambience.sound.Soundscapes;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class SoundscapeConfig {
	public static void loadSoundscape(MALogger logger, File file) {
		JsonParser parser = new JsonParser();
		Soundscape scape = new Soundscape();
		
		try {
			JsonArray soundscapeElement = parser.parse(new FileReader(file)).getAsJsonArray();
			
			for(int i=0; i<soundscapeElement.size(); i++) {
				loadSound(scape, soundscapeElement.get(i).getAsJsonObject());
			}
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			logger.error("Exception reading soundscape config: "+file.getAbsolutePath()+": "+e);
		}
		
		Soundscapes.addSoundscape(scape);
	}
	
	private static void loadSound(Soundscape scape, JsonObject sound) {
		SoundInfo si = new SoundInfo();
		
		si.setName(sound.get("Name").getAsString());
		si.setSound(sound.get("Sound").getAsString());
		si.setConditions(sound.get("Conditions").getAsString());
		si.setCooldown(sound.get("Cooldown").getAsString());
		
		if(sound.has("Restrictions")) si.setRestrictions(sound.get("Restrictions").getAsString());
		if(sound.has("Volume")) si.setVolume(sound.get("Volume").getAsString());
		if(sound.has("Pitch")) si.setRestrictions(sound.get("Pitch").getAsString());
		
		scape.addSound(si);
	}
}
