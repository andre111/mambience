/*
 * Copyright (c) 2023 Andre Schweiger
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
package me.andre111.mambience.sound;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.accessor.Accessor;

public class SoundPlayer {
	private final Accessor accessor;
	private final MALogger logger;
	private final List<ScheduledSound> scheduledSounds = new LinkedList<>();
	
	public SoundPlayer(Accessor accessor, MALogger logger) {
		this.accessor = accessor;
		this.logger = logger;
	}
	
	public void update() {
		// process scheduled sounds
		long time = System.currentTimeMillis();
		Iterator<ScheduledSound> iter = scheduledSounds.iterator();
		while(iter.hasNext()) {
			ScheduledSound sound = iter.next();
			if(time - sound.startTime >= sound.delay) {
				logger.log("Play Delayed "+sound.name);
				if(sound.global) accessor.playGlobalSound(sound.name, sound.x, sound.y, sound.z, sound.volume, sound.pitch);
				else accessor.playSound(sound.name, sound.x, sound.y, sound.z, sound.volume, sound.pitch);
				iter.remove();
			}
		}
	}
	
	public void playSound(Sound sound, double x, double y, double z, boolean global) {
		// check sound probability
		if(sound.probability() < 1 && Math.random() >= sound.probability()) return;
		
		// calculate volume and pitch
		float volume = sound.calculateRandomVolume();
		float pitch = sound.calculateRandomPitch();
		
		// schedule if delay > 0
		if(sound.delay() > 0) {
			scheduledSounds.add(new ScheduledSound(sound.name(), x, y, z, volume, pitch, global, sound.delay(), System.currentTimeMillis()));
		} else {
			logger.log("Play "+sound.name());
			if(global) accessor.playGlobalSound(sound.name(), x, y, z, volume, pitch);
			else accessor.playSound(sound.name(), x, y, z, volume, pitch);
		}
	}
	
	private static record ScheduledSound(String name, double x, double y, double z, float volume, float pitch, boolean global, int delay, long startTime) {}
}
