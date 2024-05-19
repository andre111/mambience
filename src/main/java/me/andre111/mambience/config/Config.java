/*
 * Copyright (c) 2024 Andre Schweiger
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
	private MovementConfig footsteps = new MovementConfig();
	
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
	
	public static MovementConfig movement() {
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
		public static final int DEFAULT_ENTITY_SIZE_X = 33;
		public static final int DEFAULT_ENTITY_SIZE_Y = 17;
		public static final int DEFAULT_ENTITY_SIZE_Z = 33;

		private int sizeX = DEFAULT_SIZE_X;
		private int sizeY = DEFAULT_SIZE_Y;
		private int sizeZ = DEFAULT_SIZE_X;
		private int interval = DEFAULT_INTERVAL;
		private int entitySizeX = DEFAULT_ENTITY_SIZE_X;
		private int entitySizeY = DEFAULT_ENTITY_SIZE_Y;
		private int entitySizeZ = DEFAULT_ENTITY_SIZE_Z;

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
		public int getEntitySizeX() {
			return entitySizeX;
		}
		public void setEntitySizeX(int entitySizeX) {
			this.entitySizeX = entitySizeX;
		}
		public int getEntitySizeY() {
			return entitySizeY;
		}
		public void setEntitySizeY(int entitySizeY) {
			this.entitySizeY = entitySizeY;
		}
		public int getEntitySizeZ() {
			return entitySizeZ;
		}
		public void setEntitySizeZ(int entitySizeZ) {
			this.entitySizeZ = entitySizeZ;
		}
	}
	public static class AmbientEventsConfig {
		public static final boolean DEFAULT_ENABLED = true;
		public static final float DEFAULT_VOLUME = 0.4f;
		public static final boolean DEFAULT_STOP_SOUNDS = false;
		public static final boolean DEFAULT_DISABLE_WIND = false;
		public static final boolean DEFAULT_TRIGGER_ATTACK_SOUNDS = true;
		public static final boolean DEFAULT_TRIGGER_USE_SOUNDS = true;
		public static final boolean DEFAULT_TRIGGER_HELD_ITEM_SOUNDS = true;

		private boolean enabled = DEFAULT_ENABLED;
		private float volume = DEFAULT_VOLUME;
		private boolean stopSounds = DEFAULT_STOP_SOUNDS;
		private boolean disableWind = DEFAULT_DISABLE_WIND;
		private boolean triggerAttackSounds = DEFAULT_TRIGGER_ATTACK_SOUNDS;
		private boolean triggerUseSounds = DEFAULT_TRIGGER_USE_SOUNDS;
		private boolean triggerHeldItemSounds = DEFAULT_TRIGGER_HELD_ITEM_SOUNDS;

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
		public boolean stopSounds() {
			return stopSounds;
		}
		public void setStopSounds(boolean stopSounds) {
			this.stopSounds = stopSounds;
		}
		public boolean disableWind() {
			return disableWind;
		}
		public void setDisableWind(boolean disableWind) {
			this.disableWind = disableWind;
		}
		public boolean triggerAttackSounds() {
			return triggerAttackSounds;
		}
		public void setTriggerAttackSounds(boolean triggerAttackSounds) {
			this.triggerAttackSounds = triggerAttackSounds;
		}
		public boolean triggerUseSounds() {
			return triggerUseSounds;
		}
		public void setTriggerUseSounds(boolean triggerUseSounds) {
			this.triggerUseSounds = triggerUseSounds;
		}
		public boolean triggerHeldItemSounds() {
			return triggerHeldItemSounds;
		}
		public void setTriggerHeldItemSounds(boolean triggerHeldItemSounds) {
			this.triggerHeldItemSounds = triggerHeldItemSounds;
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
	public static class MovementConfig {
		public static final boolean DEFAULT_FOOTSTEPS_ENABLED = true;
		public static final boolean DEFAULT_ARMOR_ENABLED = true;
		public static final float DEFAULT_VOLUME = 0.3f;
		public static final boolean DEFAULT_APPLY_SUGGESTIONS = true;

		private boolean footstepsEnabled = DEFAULT_FOOTSTEPS_ENABLED;
		private boolean armorEnabled = DEFAULT_FOOTSTEPS_ENABLED;
		private float volume = DEFAULT_VOLUME;
		private boolean applySuggestedSounds = DEFAULT_APPLY_SUGGESTIONS;

		public boolean footstepsEnabled() {
			return footstepsEnabled;
		}
		public void setFootstepsEnabled(boolean footstepsEnabled) {
			this.footstepsEnabled = footstepsEnabled;
		}
		public boolean armorEnabled() {
			return armorEnabled;
		}
		public void setArmorEnabled(boolean armorEnabled) {
			this.armorEnabled = armorEnabled;
		}
		public float getVolume() {
			return volume;
		}
		public void setVolume(float volume) {
			this.volume = volume;
		}

		public boolean applySuggested() {
			return applySuggestedSounds;
		}
		public void setApplySuggested(boolean enabled) {
			this.applySuggestedSounds = enabled;
		}
	}
}
