package me.andre111.mambience.effect.instance;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.effect.EffectInstance;

public class Fireflies extends EffectInstance {
	private final double x;
	private final double y;
	private final double z;
	
	private final int range;

	public Fireflies(MAPlayer player, double x, double y, double z, int range) {
		super(player, 10 * 20);
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.range = range;
	}

	@Override
	public void tick() {
		super.tick();
		
		if(Math.random() < 1/40.0) {
			getPlayer().getAccessor().addParticle("minecraft:end_rod", "", 
					x+(Math.random()*range+2+1)-range, y+Math.random()*range, z+(Math.random()*range+2+1)-range, 
					0.5*(Math.random() - 0.5), 0.2*(Math.random() - 0.5), 0.5*(Math.random() - 0.5));
		}
	}
}
