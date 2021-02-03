package me.andre111.mambience.effect.instance;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.effect.EffectInstance;

public class DustWind extends EffectInstance {
	// TODO: make this dynamic
	public static final double WIND_X;
	public static final double WIND_Z;
	static {
		double windDir = Math.random() * 2 * Math.PI;
		WIND_X = Math.sin(windDir);
		WIND_Z = Math.cos(windDir);
	}
	
	private final String type;
	private final double windX;
	private final double windZ;
	private final double x;
	private final double y;
	private final double z;

	public DustWind(MAPlayer player, String type, double windX, double windZ, double x, double y, double z) {
		super(player, 20 * 3);
		
		this.type = type;
		this.windX = windX;
		this.windZ = windZ;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void tick() {
		super.tick();
		
		if(Math.random() < 0.2) {
			double scale = 1 + Math.random()*0.2;
			getPlayer().getAccessor().addParticle("minecraft:item", type, x-2+Math.random()*5, y+1+Math.random()*2, z-2+Math.random()*5, windX*scale, 0, windZ*scale);
		}
	}
}
