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
package me.andre111.mambience.sound;

import java.util.HashSet;
import java.util.Set;

import me.andre111.mambience.player.MAPlayer;

public class Soundscapes {
	private static Set<Soundscape> scapes = new HashSet<>();
	
	
	public static void addSoundscape(Soundscape scape) {
		scapes.add(scape);
	}
	
	public static void initGlobal() {
		for(Soundscape scape : scapes) {
			scape.initGlobal();
		}
	}
	
	public static void init(MAPlayer player) {
		for(Soundscape scape : scapes) {
			scape.init(player);
		}
	}
	
	public static void update(MAPlayer player) {
		for(Soundscape scape : scapes) {
			scape.update(player);
		}
	}
}
