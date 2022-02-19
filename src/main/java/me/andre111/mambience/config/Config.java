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
	private static final int VERSION = 3;
	private static Config instance = null;
	private static File configRoot = null;
	
	private int version = VERSION;
	private boolean debugLogging = false;
	private ScannerConfig scanner = new ScannerConfig();
	private AmbientEventsConfig ambientEvents = new AmbientEventsConfig();
	private EffectsConfig effects = new EffectsConfig();
	private FootstepConfig footsteps = new FootstepConfig();
	
	public static boolean debugLogging() {
		return instance.debugLogging;
	}
	public static void setDebugLogging(boolean debugLogging) {
		instance.debugLogging = debugLogging;
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
	
	public static File getRoot() {
		return configRoot;
	}
	
	public static void initialize(MALogger logger, File configRoot) {
		if(instance != null) return;
		Config.configRoot = configRoot;
		
		try {
			// read basic config
			instance = new Config();
			boolean update = true;
			
			if(!configRoot.exists()) {
				configRoot.mkdir();
			}
			File configFile = new File(configRoot, "/config.json");
			Config existingConfig = load();
			if(existingConfig != null && existingConfig.version >= VERSION) {
				instance = existingConfig;
				update = false;
			}
			
			// save config if required
			if(update) {
				logger.error("Creating or updating config and settings, backups will be created...");
				if(configFile.exists()) Files.copy(configFile.toPath(), new File(configRoot, "/config.json_backup").toPath(), StandardCopyOption.REPLACE_EXISTING);
				else configFile.createNewFile();
				save();
			}
		} catch (Exception e) {
			logger.error("Exception reading settings: "+e);
			e.printStackTrace();
		}
	}
	
	public static Config load() throws IOException {
		File configFile = new File(configRoot, "/config.json");
		if(configFile.exists()) {
			try(BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
				return new Gson().fromJson(reader, Config.class);
			}
		}
		return null;
	}
	
	public static void save() throws IOException {
		File configFile = new File(configRoot, "/config.json");
		try(JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(configFile)))) {
			writer.setIndent("    ");
			new Gson().toJson(instance, Config.class, writer);
		}
	}
	
	public static class ScannerConfig {
		public static final int DEFAULT_SIZE_X = 11;
		public static final int DEFAULT_SIZE_Y = 9;
		public static final int DEFAULT_SIZE_Z = 11;
		public static final int DEFAULT_INTERVAL = 20;
		
		private int sizeX = DEFAULT_SIZE_X;
		private int sizeY = DEFAULT_SIZE_Y;
		private int sizeZ = DEFAULT_SIZE_X;
		private int interval = DEFAULT_INTERVAL;
		
		public int getSizeX() {
			return sizeX;
		}
		public void setSizeX(int sizeX) {
			this.sizeX = sizeX;
		}
		public int getSizeY() {
			return sizeY;
		}
		public void setSizeY(int sizeY) {
			this.sizeY = sizeY;
		}
		public int getSizeZ() {
			return sizeZ;
		}
		public void setSizeZ(int sizeZ) {
			this.sizeZ = sizeZ;
		}
		public int getInterval() {
			return interval;
		}
		public void setInterval(int interval) {
			this.interval = interval;
		}
	}
	public static class AmbientEventsConfig {
		public static final boolean DEFAULT_ENABLED = true;
		public static final float DEFAULT_VOLUME = 0.4f;
		public static final boolean DEFAULT_STOP_SOUNDS = false;
		
		private boolean enabled = DEFAULT_ENABLED;
		private float volume = DEFAULT_VOLUME;
		private boolean stopSounds = DEFAULT_STOP_SOUNDS;
		
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public float getVolume() {
			return volume;
		}
		public void setVolume(float volume) {
			this.volume = volume;
		}
		public boolean isStopSounds() {
			return stopSounds;
		}
		public void setStopSounds(boolean stopSounds) {
			this.stopSounds = stopSounds;
		}
	}
	public static class EffectsConfig {
		public static final boolean DEFAULT_ENABLED = true;
		public static final int DEFAULT_SIZE_X = 36;
		public static final int DEFAULT_SIZE_Y = 18;
		public static final int DEFAULT_SIZE_Z = 36;
		public static final int DEFAULT_RANDOM_TICKS = 384;
		
		private boolean enabled = DEFAULT_ENABLED;
		private int sizeX = DEFAULT_SIZE_X;
		private int sizeY = DEFAULT_SIZE_Y;
		private int sizeZ = DEFAULT_SIZE_Z;
		private int randomTicks = DEFAULT_RANDOM_TICKS;

		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public int getSizeX() {
			return sizeX;
		}
		public void setSizeX(int sizeX) {
			this.sizeX = sizeX;
		}
		public int getSizeY() {
			return sizeY;
		}
		public void setSizeY(int sizeY) {
			this.sizeY = sizeY;
		}
		public int getSizeZ() {
			return sizeZ;
		}
		public void setSizeZ(int sizeZ) {
			this.sizeZ = sizeZ;
		}
		public int getRandomTicks() {
			return randomTicks;
		}
		public void setRandomTicks(int randomTicks) {
			this.randomTicks = randomTicks;
		}
	}
	public static class FootstepConfig {
		public static final boolean DEFAULT_ENABLED = true;
		public static final float DEFAULT_VOLUME = 0.3f;
		
		private boolean enabled = DEFAULT_ENABLED;
		private float volume = DEFAULT_VOLUME;
		
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public float getVolume() {
			return volume;
		}
		public void setVolume(float volume) {
			this.volume = volume;
		}
	}
}
