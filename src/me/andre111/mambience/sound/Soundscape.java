package me.andre111.mambience.sound;

import java.util.ArrayList;

import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.player.MAPlayer;
import me.andre111.mambience.script.MAScriptEngine;
import me.andre111.mambience.script.MAScripting;

public class Soundscape {
	private ArrayList<SoundInfo> sounds = new ArrayList<SoundInfo>();
	private boolean initialised = false;
	
	public void init(MAPlayer maplayer) {
		//TODO: Maybe make compiling asnyc?
		MAScriptEngine se = maplayer.getScriptEngine();
		
		for(SoundInfo si : sounds) {
			si.init(se);
			startCooldown(maplayer, se, si);
		}
		
		initialised = true;
	}
	
	public void update(MAPlayer maplayer) {
		//Wait for compile to finish because update is async
		if(!initialised) return;
		
		MAScriptEngine se = maplayer.getScriptEngine();
		//TODO: offset location to make sound "mono", not positioned at a certain spot in the world, e.g. y + 5000
		
		for(SoundInfo si : sounds) {
			if(conditionsMet(se, si)) {
				if(updateCooldown(maplayer, si) <= 0) {
					float volume = ((Number) se.evalJS(si.getVolume())).floatValue();
					float pitch = ((Number) se.evalJS(si.getPitch())).floatValue();
					
					System.out.println("Play sound "+si.getSound());
					maplayer.playSound(si.getSound(), volume, pitch);
					
					//sadly you cannot fade sounds in and out
					
					startCooldown(maplayer, se, si);
				}
			} else if (getCooldown(maplayer, si) > 0 /*&& isSoundRestricted(so it doesn't get cut of in so many cases?)*/) {
				//TODO: needs fading in and out, sadly not possible with current protocol
				//      for now disabled with config option to reenable, sound stopping without fadeout is just to abrupt
				if(EngineConfig.STOPSOUNDS) {
					System.out.println("Stop sound "+si.getSound());
					maplayer.stopSound(si.getSound());
					resetCooldown(maplayer, si);
				}
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
	
	private int updateCooldown(MAPlayer maplayer, SoundInfo si) {
		int value = maplayer.getCooldown(si.getName());
		value = value - 1;
		maplayer.setCooldown(si.getName(), value);
		return value;
	}
	
	private void startCooldown(MAPlayer maplayer, MAScriptEngine se, SoundInfo si) {
		maplayer.setCooldown(si.getName(), getIntFunctionResult(se, "Internal_Cooldown_"+si.getName()));
	}
	
	private int getCooldown(MAPlayer maplayer, SoundInfo si) {
		return maplayer.getCooldown(si.getName());
	}
	
	private void resetCooldown(MAPlayer maplayer, SoundInfo si) {
		maplayer.setCooldown(si.getName(), 0);
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
							 +"}");
			se.compileScript("function Internal_Cooldown_"+name+"() {"
							+ "   return "+getCooldown()+";"
							+ "}");
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
