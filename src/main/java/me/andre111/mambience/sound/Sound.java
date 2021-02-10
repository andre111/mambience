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
package me.andre111.mambience.sound;

public class Sound {
	private final String name;
	private final float volumeMin;
	private final float volumeMax;
	private final float pitchMin;
	private final float pitchMax;
	
	private final int delay;
	private final double probability;
	
	public Sound(String name, float volumeMin, float volumeMax, float pitchMin,float pitchMax, int delay, double probability) {
		this.name = name;
		this.volumeMin = volumeMin;
		this.volumeMax = volumeMax;
		this.pitchMin = pitchMin;
		this.pitchMax = pitchMax;
		this.delay = delay;
		this.probability = probability;
		
		// TODO> perform some validation checks
	}

	public String getName() {
		return name;
	}
	
	public float getVolume() {
		return volumeMin + (float) (Math.random() * (volumeMax - volumeMin));
	}
	
	public float getPitch() {
		return pitchMin + (float) (Math.random() * (pitchMax - pitchMin));
	}

	public int getDelay() {
		return delay;
	}

	public double getProbability() {
		return probability;
	}
}
