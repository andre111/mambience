package me.andre111.mambience.footstep;

public class FSMaterial {
	private final FSSound[] wanderSounds;
	private final FSSound[] walkSounds;
	private final FSSound[] runSounds;
	private final FSSound[] jumpSounds;
	private final FSSound[] landSounds;
	
	public FSMaterial(FSSound[] wanderSounds, FSSound[] walkSounds, FSSound[] runSounds, FSSound[] jumpSounds, FSSound[] landSounds) {
		this.wanderSounds = wanderSounds;
		this.walkSounds = walkSounds;
		this.runSounds = runSounds;
		this.jumpSounds = jumpSounds;
		this.landSounds = landSounds;
	}

	public FSSound[] getWanderSounds() {
		return wanderSounds;
	}

	public FSSound[] getWalkSounds() {
		return walkSounds;
	}

	public FSSound[] getRunSounds() {
		return runSounds;
	}

	public FSSound[] getJumpSounds() {
		return jumpSounds;
	}

	public FSSound[] getLandSounds() {
		return landSounds;
	}
}
