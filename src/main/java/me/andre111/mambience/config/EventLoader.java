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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import me.andre111.mambience.MALogger;
import me.andre111.mambience.MAmbience;
import me.andre111.mambience.ambient.AmbientEvent;
import me.andre111.mambience.condition.Condition;
import me.andre111.mambience.sound.Sound;

public final class EventLoader {
	private static final Map<String, Set<AmbientEvent>> EVENTS = new HashMap<>();
	
	public static Set<AmbientEvent> getEvents(String trigger) {
		return EVENTS.getOrDefault(trigger, Set.of());
	}
	
	public static void forAllEvents(Consumer<AmbientEvent> consumer) {
		for(Set<AmbientEvent> set : EVENTS.values()) {
			for(AmbientEvent event : set) {
				consumer.accept(event);
			}
		}
	}
	
	public static void reset() {
		EVENTS.clear();
	}
	
	public static void loadEvent(String id, JsonObject obj) {
		String trigger = ConfigUtil.getString(obj, "trigger", "SECOND");
		
		EVENTS.computeIfAbsent(trigger, k -> new HashSet<>()).add(loadEvent(MAmbience.getLogger(), id, obj));
	}
	
	private static AmbientEvent loadEvent(MALogger logger, String id, JsonObject obj) {
		Sound[] sounds = ConfigUtil.loadSounds(obj.get("sound"), Config.ambientEvents().getVolume());
		List<Condition> conditions = ConfigUtil.loadConditions(logger, obj.get("conditions").getAsJsonArray());
		List<Condition> restrictions = ConfigUtil.loadConditions(logger, obj.get("restrictions").getAsJsonArray());
		int cooldownMin = ConfigUtil.getInt(obj, "cooldownMin", 1);
		int cooldownMax = ConfigUtil.getInt(obj, "cooldownMax", 1);
		
		return new AmbientEvent(id, sounds, conditions, restrictions, cooldownMin, cooldownMax);
	}
}
