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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import me.andre111.mambience.MALogger;

public final class Config {
	private static final int VERSION = 1;
	private static Config instance = null;
	
	private int version = VERSION;
	private boolean debugLogging = false;
	private ScannerConfig scanner = new ScannerConfig();
	private AmbientEventsConfig ambientEvents = new AmbientEventsConfig();
	private EffectsConfig effects = new EffectsConfig();
	private FootstepConfig footsteps = new FootstepConfig();
	
	public static boolean debugLogging() {
		return instance.debugLogging;
	}
	
	public static ScannerConfig scanner() {
		return instance.scanner;
	}
	
	public static AmbientEventsConfig ambientEvents() {
		return instance.ambientEvents;
	}
	
	public static EffectsConfig effects() {
		return instance.effects;
	}
	
	public static FootstepConfig footsteps() {
		return instance.footsteps;
	}
	
	public static void initialize(MALogger logger, File configRoot) {
		if(instance != null) return;

		try {
			// read basic config
			instance = new Config();
			boolean update = true;
			
			if(!configRoot.exists()) {
				configRoot.mkdir();
			}
			File configFile = new File(configRoot, "/config.json");
			if(configFile.exists()) {
				try(CommentSkippingReader reader = new CommentSkippingReader(new BufferedReader(new FileReader(configFile)))) {
					Config existingConfig = new Gson().fromJson(reader.readAllLines("\n"), Config.class);
					if(existingConfig != null && existingConfig.version >= VERSION) {
						instance = existingConfig;
						update = false;
					}
				}
			}
			
			// save config if required
			if(update) {
				logger.error("Creating or updating config and settings, backups will be created...");
				if(configFile.exists()) Files.copy(configFile.toPath(), new File(configRoot, "/config.json_backup").toPath(), StandardCopyOption.REPLACE_EXISTING);
				else configFile.createNewFile();
				try(JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(configFile)))) {
					writer.setIndent("    ");
					new Gson().toJson(instance, Config.class, writer);
				}
			}
			
			// export settings
			exportSettings(configRoot, update);
			DataLoader.loadData(logger, new File(configRoot, "/settings/data.json"));
			EventLoader.loadEvents(logger, new File(configRoot, "/settings/events.json"));
			EffectLoader.loadEffects(logger, new File(configRoot, "/settings/effects.json"));
			FootstepLoader.loadFootsteps(logger, new File(configRoot, "/settings/footsteps.json"));
		} catch (Exception e) {
			logger.error("Exception reading settings: "+e);
			e.printStackTrace();
		}
	}
	
	private static void exportSettings(File folder, boolean update) {
		exportSingleFile(folder, "/settings/data.json", update);
		exportSingleFile(folder, "/settings/events.json", update);
		exportSingleFile(folder, "/settings/effects.json", update);
		exportSingleFile(folder, "/settings/footsteps.json", update);
	}
	private static void exportSingleFile(File folder, String path, boolean update) {
		File file = new File(folder, path);
		if(!file.exists() || update) {
			try {
				// backup existing file or create new file
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				if(file.exists()) {
					Files.copy(file.toPath(), new File(folder, path+"_backup").toPath(), StandardCopyOption.REPLACE_EXISTING);
				} else {
					file.createNewFile();
				}
				// export settings
				Files.copy(Config.class.getResourceAsStream(path), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static class ScannerConfig {
		private int sizeX = 11;
		private int sizeY = 9;
		private int sizeZ = 11;
		private int interval = 20;
		
		public int getSizeX() {
			return sizeX;
		}
		public int getSizeY() {
			return sizeY;
		}
		public int getSizeZ() {
			return sizeZ;
		}
		public int getInterval() {
			return interval;
		}
	}
	public static class AmbientEventsConfig {
		private boolean enabled = true;
		private float volume = 0.5f;
		private boolean stopSounds = false;
		
		public boolean isEnabled() {
			return enabled;
		}
		public float getVolume() {
			return volume;
		}
		public boolean isStopSounds() {
			return stopSounds;
		}
	}
	public static class EffectsConfig {
		private boolean enabled = true;
		private int sizeX = 36;
		private int sizeY = 18;
		private int sizeZ = 36;
		private int randomTicks = 384;

		public boolean isEnabled() {
			return enabled;
		}
		public int getSizeX() {
			return sizeX;
		}
		public int getSizeY() {
			return sizeY;
		}
		public int getSizeZ() {
			return sizeZ;
		}
		public int getRandomTicks() {
			return randomTicks;
		}
	}
	public static class FootstepConfig {
		private boolean enabled = true;
		private float volume = 0.7f;
		
		public boolean isEnabled() {
			return enabled;
		}
		public float getVolume() {
			return volume;
		}
	}
}
