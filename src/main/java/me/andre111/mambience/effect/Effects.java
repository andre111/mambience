package me.andre111.mambience.effect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.accessor.Accessor;

public class Effects {
	private static Random random = new Random();
	private static Set<Effect> effects = new HashSet<>();
	private static List<EffectInstance> instances = new ArrayList<>();

	public static void reset() {
		effects.clear();
	}
	
	public static void addEffect(Effect effect) {
		effects.add(effect);
	}
	
	// call to tick effect instances
	public static void tick() {
		Iterator<EffectInstance> iter = instances.iterator();
		while(iter.hasNext()) {
			EffectInstance instance = iter.next();
			instance.tick();
			
			if(!instance.isAlive()) {
				iter.remove();
			}
		}
	}
	
	// called to check for new effects
	public static void update(MAPlayer player) {
		Accessor accessor = player.getAccessor();
		
		// DO ACTUAL BLOCK TESTING
		int bx = (int) accessor.getX();
		int by = (int) accessor.getY();
		int bz = (int) accessor.getZ();
		for(int i=0; i<256; i++) {
			int ox = random.nextInt(26 + 1) - 13;
			int oy = random.nextInt(20 + 1) - 10;
			int oz = random.nextInt(26 + 1) - 13;
			
			String block = player.getAccessor().getBlock(bx+ox, by+oy, bz+oz);
			
			for(Effect effect : effects) {
				effect.update(player, block, bx+ox, by+oy, bz+oz);
			}
		}
	}
	
	public static void addInstance(EffectInstance instance) {
		instances.add(instance);
	}
}
