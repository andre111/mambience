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
				if(sound.global) accessor.playGlobalFootstepSound(sound.name, sound.x, sound.y, sound.z, sound.volume, sound.pitch);
				else accessor.playSound(sound.name, sound.x, sound.y, sound.z, sound.volume, sound.pitch);
				iter.remove();
			}
		}
	}
	
	public void playSound(Sound sound, double x, double y, double z, boolean global) {
		// check sound probability
		if(sound.getProbability() < 1 && Math.random() >= sound.getProbability()) return;
		
		// calculate volume and pitch
		float volume = sound.getVolume();
		float pitch = sound.getPitch();
		
		// schedule if delay > 0
		if(sound.getDelay() > 0) {
			scheduledSounds.add(new ScheduledSound(sound.getName(), x, y, z, volume, pitch, global, sound.getDelay()));
		} else {
			logger.log("Play "+sound.getName());
			if(global) accessor.playGlobalFootstepSound(sound.getName(), x, y, z, volume, pitch);
			else accessor.playSound(sound.getName(), x, y, z, volume, pitch);
		}
	}
	
	private static final class ScheduledSound {
		private final String name;
		private final double x;
		private final double y;
		private final double z;
		private final float volume;
		private final float pitch;
		private final boolean global;
		
		private final int delay;
		private final long startTime;
		
		public ScheduledSound(String name, double x, double y, double z, float volume, float pitch, boolean global, int delay) {
			this.name = name;
			this.x = x;
			this.y = y;
			this.z = z;
			this.volume = volume;
			this.pitch = pitch;
			this.global = global;
			
			this.delay = delay;
			this.startTime = System.currentTimeMillis();
		}
	}
}
