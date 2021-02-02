package me.andre111.mambience.effect.instance;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.effect.EffectInstance;

public class FlameJet extends EffectInstance {
	private final String type;
	private final int strength;
	private final double x;
	private final double y;
	private final double z;
	
	private boolean firstTick;

	public FlameJet(MAPlayer player, int strength, double x, double y, double z) {
		super(player, (int) (Math.random() * strength + 2) * 20);
		
		this.type = Math.random() < 0.1 ? "minecraft:lava" : "minecraft:flame";
		this.strength = strength;
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.firstTick = true;
	}

	@Override
	public void tick() {
		super.tick();
		
		if(this.firstTick) {
			this.firstTick = false;
			getPlayer().getAccessor().playSound("minecraft:block.fire.ambient", x, y, z, 1, 1);
		}
		
		if(this.getLife() % 3 == 0) {
			double velocityY = strength/12.0;
			getPlayer().getAccessor().addParticle(type, "", x, y, z, 0, velocityY, 0);
			getPlayer().getAccessor().addParticle("minecraft:smoke", "", x, y, z, 0, velocityY, 0);
			

			getPlayer().getAccessor().addParticle(type, "", x-0.25, y+Math.random()*0.5, z, +0.02/strength, velocityY, 0);
			getPlayer().getAccessor().addParticle(type, "", x+0.25, y+Math.random()*0.5, z, -0.02/strength, velocityY, 0);
			getPlayer().getAccessor().addParticle(type, "", x, y+Math.random()*0.5, z-0.25, 0, velocityY, +0.02/strength);
			getPlayer().getAccessor().addParticle(type, "", x, y+Math.random()*0.5, z+0.25, 0, velocityY, -0.02/strength);
		}
	}
}
