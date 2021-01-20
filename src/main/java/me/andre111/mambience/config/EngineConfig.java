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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.andre111.mambience.MALogger;

public final class EngineConfig {
	public static int SIZEX = 11;
	public static int SIZEY = 9;
	public static int SIZEZ = 11;
	public static int INTERVAL = 20;
	public static float GLOBALVOLUME = 0.5f;
	public static boolean STOPSOUNDS = false;
	public static boolean DEBUGLOGGING = false;
	
	private static boolean initialized = false;
	
	public static void initialize(MALogger logger, File configRoot) {
		if(initialized) return;
		initialized = true;
		
		exportSettings(configRoot);
		JsonParser parser = new JsonParser();
		
		File engine = new File(configRoot, "/settings/engine.json");
		try(CommentSkippingReader reader = new CommentSkippingReader(new BufferedReader(new FileReader(engine)))) {
			JsonObject engineElement = parser.parse(reader.readAllLines("\n")).getAsJsonObject();
			
			loadSettings(engineElement.get("Settings").getAsJsonObject());

			DataConfig.loadData(logger, new File(configRoot, "/settings/data.json"));
			SoundConfig.loadSounds(logger, new File(configRoot, "/settings/sounds.json"));
		} catch (Exception e) {
			logger.error("Exception reading engine config: "+e);
			e.printStackTrace();
		}
	}
	
	private static void exportSettings(File folder) {
		if(!folder.exists()) {
			folder.mkdir();
		}
		
		exportSingleFile(folder, "/settings/engine.json");
		exportSingleFile(folder, "/settings/data.json");
		exportSingleFile(folder, "/settings/sounds.json");
	}
	private static void exportSingleFile(File folder, String path) {
		File file = new File(folder, path);
		if(!file.exists()) {
			try {
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				Files.copy(EngineConfig.class.getResourceAsStream(path), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void loadSettings(JsonObject element) {
		SIZEX = element.get("ScannnerX").getAsInt();
		SIZEY = element.get("ScannnerY").getAsInt();
		SIZEZ = element.get("ScannnerZ").getAsInt();
		if(element.has("ScannnerInterval")) {
			INTERVAL = element.get("ScannnerInterval").getAsInt();
		}
		if(element.has("GlobalVolume")) {
			GLOBALVOLUME = element.get("GlobalVolume").getAsFloat();
		}
		STOPSOUNDS = element.get("StopSounds").getAsBoolean();
		if(element.has("DebugLogging")) {
			DEBUGLOGGING = element.get("DebugLogging").getAsBoolean();
		}
	}
}
