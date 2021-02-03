package me.andre111.mambience.footstep;

public class FSSound {
	private final String name;
	private final float volumeMin;
	private final float volumeMax;
	private final float pitchMin;
	private final float pitchMax;
	
	private final int delay;
	private final double probability;
	
	public FSSound(String name, float volumeMin, float volumeMax, float pitchMin,float pitchMax, int delay, double probability) {
		this.name = name;
		this.volumeMin = volumeMin;
		this.volumeMax = volumeMax;
		this.pitchMin = pitchMin;
		this.pitchMax = pitchMax;
		this.delay = delay;
		this.probability = probability;
	}

	public String getName() {
		return name;
	}

	public float getVolumeMin() {
		return volumeMin;
	}

	public float getVolumeMax() {
		return volumeMax;
	}

	public float getPitchMin() {
		return pitchMin;
	}

	public float getPitchMax() {
		return pitchMax;
	}

	public int getDelay() {
		return delay;
	}

	public double getProbability() {
		return probability;
	}
}
