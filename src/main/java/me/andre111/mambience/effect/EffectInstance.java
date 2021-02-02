package me.andre111.mambience.effect;

import me.andre111.mambience.MAPlayer;

public class EffectInstance {
	private MAPlayer player;
	private int life;
	
	public EffectInstance(MAPlayer player, int life) {
		this.player = player;
		this.life = life;
	}
	
	public void tick() {
		this.life--;
	}
	
	public boolean isAlive() {
		return this.life > 0;
	}
	
	public MAPlayer getPlayer() {
		return player;
	}
	
	public int getLife() {
		return life;
	}
}
