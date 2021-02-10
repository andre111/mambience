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
package me.andre111.mambience.ambient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.condition.Condition;
import me.andre111.mambience.config.Config;
import me.andre111.mambience.sound.Sound;

public final class AmbientEvent {
	private static final Random RANDOM = new Random();
	
	private final String id;
	private final Sound[] sounds;
	private final List<Condition> conditions;
	private final List<Condition> restrictions;
	private final int cooldownMin;
	private final int cooldownMax;
	
	public AmbientEvent(String id, Sound[] sounds, List<Condition> conditions, List<Condition> restrictions, int cooldownMin, int cooldownMax) {
		this.id = id;
		this.sounds = sounds;
		this.conditions = new ArrayList<>(conditions);
		this.restrictions = new ArrayList<>(restrictions);
		this.cooldownMin = cooldownMin;
		this.cooldownMax = cooldownMax;
		
		// perform some validation checks
		//TODO: extend
		if(cooldownMin < 0) {
			throw new IllegalArgumentException("Cooldown Minimum cannot be negative");
		}
		if(cooldownMax < 0) {
			throw new IllegalArgumentException("Cooldown Maximum cannot be negative");
		}
		if(cooldownMax < cooldownMin) {
			throw new IllegalArgumentException("Cooldown Minimum cannot be larger than Cooldown Maximum");
		}
	}

	public void update(MAPlayer maplayer) {
		if(conditionsMet(maplayer)) {
			if(maplayer.updateCooldown(id) <= 0) {
				for(Sound sound : sounds) {
					maplayer.getSoundPlayer().playSound(sound, maplayer.getAccessor().getX(), maplayer.getAccessor().getY(), maplayer.getAccessor().getZ(), false);
				}
				maplayer.setCooldown(id, cooldownMin + RANDOM.nextInt(cooldownMax - cooldownMin + 1));
			}
		} else if (Config.ambientEvents().isStopSounds() && maplayer.getCooldown(id) > 0 /*&& isRestricted(maplayer)(so it doesn't get cut of in so many cases?)*/) {
			//TODO: needs fading in and out, sadly not possible with current protocol
			//      for now disabled with config option to reenable, sound stopping without fadeout is just to abrupt
			for(Sound sound : sounds) {
				maplayer.getLogger().log("Stop sound "+sound.getName());
				maplayer.getAccessor().stopSound(sound.getName());
			}
			maplayer.setCooldown(id, 0);
		}
	}
	
	private boolean conditionsMet(MAPlayer maplayer) {
		return isInConditions(maplayer) && !isRestricted(maplayer);
	}
	
	private boolean isInConditions(MAPlayer maplayer) {
		for(Condition condition : conditions) {
			if(!condition.matches(maplayer)) return false;
		}
		return true;
	}
	
	private boolean isRestricted(MAPlayer maplayer) {
		for(Condition restriction : restrictions) {
			if(restriction.matches(maplayer)) return true;
		}
		return false;
	}
}
