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

import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.player.MAPlayer;
import me.andre111.mambience.script.MAScriptEngine;
import me.andre111.mambience.script.MAScripting;

public class Soundscape {
	private Set<SoundInfo> sounds = new HashSet<>();
	private boolean initialised = false;
	
	public void initGlobal() {
		//TODO: Maybe make compiling asnyc?
		MAScriptEngine se = MAScriptEngine.getInstance();
		
		for(SoundInfo si : sounds) {
			si.init(se);
		}
		
		initialised = true;
	}
	
	public void init(MAPlayer maplayer) {
		MAScriptEngine se = MAScriptEngine.getInstance();
		
		for(SoundInfo si : sounds) {
			startCooldown(maplayer, se, si);
		}
	}
	
	public void update(MAPlayer maplayer) {
		//Wait for compile to finish because update is async
		if(!initialised) return;
		
		MAScriptEngine se = MAScriptEngine.getInstance();
		//TODO: offset location to make sound "mono", not positioned at a certain spot in the world, e.g. y + 5000
		
		for(SoundInfo si : sounds) {
			if(conditionsMet(se, si)) {
				if(maplayer.updateCooldown(si.getName()) <= 0) {
					float volume = ((Number) se.invokeFunction("Internal_Volume_"+si.getName())).floatValue() * EngineConfig.GLOBALVOLUME;
					float pitch = ((Number) se.invokeFunction("Internal_Pitch_"+si.getName())).floatValue();
					
					maplayer.getLogger().log("Play sound "+si.getSound()+" at "+volume);
					maplayer.getAccessor().playSound(si.getSound(), volume, pitch);
					
					//sadly you cannot fade sounds in and out
					
					startCooldown(maplayer, se, si);
				}
			} else if (EngineConfig.STOPSOUNDS && maplayer.getCooldown(si.getName()) > 0 /*&& isSoundRestricted(so it doesn't get cut of in so many cases?)*/) {
				//TODO: needs fading in and out, sadly not possible with current protocol
				//      for now disabled with config option to reenable, sound stopping without fadeout is just to abrupt
				maplayer.getLogger().log("Stop sound "+si.getSound());
				maplayer.getAccessor().stopSound(si.getSound());
				maplayer.setCooldown(si.getName(), 0);
			}
		}
	}
	
	private boolean conditionsMet(MAScriptEngine se, SoundInfo si) {
		return getBooleanFunctionResult(se, "Internal_Function_"+si.getName());
	}

	private boolean getBooleanFunctionResult(MAScriptEngine se, String name) {
		Object value = se.invokeFunction(name);
		if(value == null) return false;
		if(value instanceof Boolean) return (Boolean) value;
		if(value instanceof Number) return ((Number) value).intValue() == 0;
		return false;
	}
	
	private int getIntFunctionResult(MAScriptEngine se, String name) {
		Object value = se.invokeFunction(name);
		if(value == null) return 0;
		if(value instanceof Number) return ((Number) value).intValue();
		return 0;
	}
	
	private void startCooldown(MAPlayer maplayer, MAScriptEngine se, SoundInfo si) {
		maplayer.setCooldown(si.getName(), getIntFunctionResult(se, "Internal_Cooldown_"+si.getName()));
	}
	
	public void addSound(SoundInfo sound) {
		sounds.add(sound);
	}
	
	public static class SoundInfo {
		private String name;
		private String sound;
		private String conditions;
		private String restrictions = "false";
		private String volume = "1.0";
		private String pitch = "1.0";
		private String cooldown;
		
		private void init(MAScriptEngine se) {
			se.compileScript("function Internal_Function_"+name+"() {"
							+"   return ("+getConditions()+") && !("+getRestrictions()+");"
							+"}"
							+"function Internal_Cooldown_"+name+"() {"
							+"   return "+getCooldown()+";"
							+"}"
							+"function Internal_Volume_"+name+"() {"
							+"   return "+getVolume()+";"
							+"}"
							+"function Internal_Pitch_"+name+"() {"
							+"   return "+getPitch()+";"
							+"}");
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getSound() {
			return sound;
		}
		public void setSound(String sound) {
			this.sound = sound;
		}
		public String getConditions() {
			return conditions;
		}
		public void setConditions(String conditions) {
			this.conditions = MAScripting.expandMacros(conditions);
		}
		public String getRestrictions() {
			return restrictions;
		}
		public void setRestrictions(String restrictions) {
			this.restrictions = MAScripting.expandMacros(restrictions);
		}
		public String getVolume() {
			return volume;
		}
		public void setVolume(String volume) {
			this.volume = MAScripting.expandMacros(volume);
		}
		public String getPitch() {
			return pitch;
		}
		public void setPitch(String pitch) {
			this.pitch = MAScripting.expandMacros(pitch);
		}
		public String getCooldown() {
			return cooldown;
		}
		public void setCooldown(String cooldown) {
			this.cooldown = MAScripting.expandMacros(cooldown);
		}
	}
}
