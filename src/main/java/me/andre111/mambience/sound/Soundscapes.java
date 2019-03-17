package me.andre111.mambience.sound;

import java.util.ArrayList;

import me.andre111.mambience.player.MAPlayer;

public class Soundscapes {
	private static ArrayList<Soundscape> scapes = new ArrayList<Soundscape>();
	
	
	public static void addSoundscape(Soundscape scape) {
		scapes.add(scape);
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
