package me.andre111.mambience.effect.instance;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.effect.EffectInstance;

public class FallingLeaves extends EffectInstance {
	private final String block;
	private final double x;
	private final double y;
	private final double z;

	public FallingLeaves(MAPlayer player, String block, double x, double y, double z) {
		super(player, 6 * 20);
		
		this.block = block;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void tick() {
		super.tick();
		
		if(Math.random() < 1/40.0) {
			getPlayer().getAccessor().addParticle("minecraft:block", block, x+Math.random(), y-0.05, z+Math.random(), 0, 0, 0);
		}
	}
}
