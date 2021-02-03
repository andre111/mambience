package me.andre111.mambience.footstep;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.andre111.mambience.accessor.Accessor;
import me.andre111.mambience.config.FootstepConfig;

public class Footsteps {	
	private final Accessor accessor;
	private final List<ScheduledSound> scheduledSounds;
	
	private double accumulatedDistance;
	private boolean wasPracticallyStanding;
	private boolean isInAir;
	private double fallDistance;
	
	private boolean rightFoot;
	
	public Footsteps(Accessor accessor) {
		this.accessor = accessor;
		this.scheduledSounds = new LinkedList<>();
	}
	
	public void update() {
		// calculate moved distance and horizontal speed
		double dx = accessor.getDeltaX();
		double dy = accessor.getDeltaY();
		double dz = accessor.getDeltaZ();
		double speed = Math.sqrt(dx*dx + dz*dz);
		double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
		
		// process "events"
		updateSteps(speed, distance);
		updateJumping(speed, distance);
		//TODO: walking through stuff (plants, cobwebs, ...)
		
		// process scheduled sounds
		long time = System.currentTimeMillis();
		Iterator<ScheduledSound> iter = scheduledSounds.iterator();
		while(iter.hasNext()) {
			ScheduledSound sound = iter.next();
			if(time - sound.startTime >= sound.delay) {
				//System.out.println("Play Delayed "+sound.name);
				accessor.playGlobalFootstepSound(sound.name, sound.x, sound.y, sound.z, sound.volume, sound.pitch);
				iter.remove();
			}
		}
	}
	
	private void updateSteps(double speed, double distance) {
		//TODO...
		if(accessor.isSneaking() || accessor.isSubmerged() || !accessor.isOnGround()) return;
		accumulatedDistance += speed;
		
		// play "wander" sound when the player is only tapping the movement keys
		boolean isPracticallyStanding = speed < 0.01;
		if(!isPracticallyStanding && wasPracticallyStanding) {
			playEvent(FSEvent.WANDER, false, -1/16.0);
		}
		wasPracticallyStanding = isPracticallyStanding;
		
		// determine stride distance (TODO: configurable values)
		double strideDistance = 1.5;
		FSEvent event = null;
		//TODO: check for other situations (i.e. on ladder or moving on steps
		
		// determine event
		if(event == null) {
			event = speed >= 0.22f ? FSEvent.RUN : FSEvent.WALK;
		}
		
		// play sound
		if(accumulatedDistance >= strideDistance) {
			accumulatedDistance -= strideDistance;
			
			if(event != null) {
				playEvent(event, false, 0);
				rightFoot = !rightFoot;
			}
		}
	}
	
	private void updateJumping(double speed, double distance) {
		//TODO...
		if(accessor.isOnGround() == isInAir) {
			isInAir = !isInAir;
			
			// trigger jumping effects
			if(isInAir && accessor.isJumping() && accessor.getDeltaY() > 0) {
				playEvent(FSEvent.JUMP, speed > 0.05, -2/16.0); //TODO: configurable value
			}
			
			// trigger landing effects
			if(!isInAir && fallDistance > 0.01) {
				playEvent(FSEvent.LAND, fallDistance > 2.8, -2/16.0); //TODO: configurable value
			}
		}
		
		// track falling distance
		if(isInAir) {
			if(accessor.getDeltaY() < 0) fallDistance += -accessor.getDeltaY();
		} else {
			fallDistance = 0;
		}
	}
	
	private void playEvent(FSEvent event, boolean doubleFooted, double yOffset) {
		//System.out.println(event);
		findAndPlaySounds(event, rightFoot, yOffset);
		if(doubleFooted) findAndPlaySounds(event, !rightFoot, yOffset);
	}
	
	private void findAndPlaySounds(FSEvent event, boolean rightFoot, double yOffset) {
		// determine foot location
		double feetDistance = rightFoot ? -0.2 : 0.2; //TODO: configurable
		double rotation = accessor.getRotation();
		
		double footX = accessor.getX() + Math.cos(rotation) * feetDistance;
		double footY = accessor.getY() + yOffset;
		double footZ = accessor.getZ() + Math.sin(rotation) * feetDistance;
		
		int footBlockX = (int) Math.floor(footX);
		int footBlockY = (int) Math.floor(footY);
		int footBlockZ = (int) Math.floor(footZ);
		
		// determine block / materials
		//TODO: this might need more advanced "area" checking when walking on block edges
		String materialNames = FootstepConfig.BLOCK_MAP.get(accessor.getBlock(footBlockX, footBlockY, footBlockZ));
		if(materialNames == null || materialNames.isEmpty()) materialNames = FootstepConfig.BLOCK_MAP.get(accessor.getBlock(footBlockX, footBlockY-1, footBlockZ));
		if(materialNames == null) return;

		playSounds(event, footX, footY, footZ, materialNames.split(","));
		
		// determine armor materials
		String armorMaterialNames = FootstepConfig.ARMOR_MAP.get(accessor.getArmor(2));
		if(armorMaterialNames == null) armorMaterialNames = FootstepConfig.ARMOR_MAP.get(accessor.getArmor(1));
		String feetMaterialNames = FootstepConfig.ARMOR_MAP.get(accessor.getArmor(0));
		
		if(armorMaterialNames != null) {
			playSounds(event, footX, footY, footZ, armorMaterialNames.split(","));
		}
		if(feetMaterialNames != null && !feetMaterialNames.equals(armorMaterialNames)) {
			playSounds(event, footX, footY, footZ, feetMaterialNames.split(","));
		}
	}

	private void playSounds(FSEvent event, double x, double y, double z, String[] materialNames) {
		for(String materialName : materialNames) {
			FSMaterial material = FootstepConfig.MATERIALS.get(materialName);
			if(material != null) playSounds(event, x, y, z, material);
		}
	}
	
	private void playSounds(FSEvent event, double x, double y, double z, FSMaterial material) {
		// determine sounds
		FSSound[] sounds = null;
		switch(event) {
		case WANDER:
			sounds = material.getWanderSounds();
			break;
		case WALK:
			sounds = material.getWalkSounds();
			break;
		case RUN:
			sounds = material.getRunSounds();
			break;
		case JUMP:
			sounds = material.getJumpSounds();
			break;
		case LAND:
			sounds = material.getLandSounds();
			break;
		default:
			break;
		}
		if(sounds == null) return;
		
		// play sounds
		for(FSSound sound : sounds) {
			// check sound probability
			if(sound.getProbability() < 1 && Math.random() >= sound.getProbability()) continue;
			
			// calculate volume and pitch
			float volume = (float) (sound.getVolumeMin() + Math.random() * (sound.getVolumeMax() - sound.getVolumeMin()));
			float pitch = (float) (sound.getPitchMin() + Math.random() * (sound.getPitchMax() - sound.getPitchMin()));
			
			// schedule if delay > 0
			if(sound.getDelay() > 0) {
				scheduledSounds.add(new ScheduledSound(sound.getName(), x, y, z, volume, pitch, sound.getDelay()));
			} else {
				//System.out.println("Play "+sound.getName());
				accessor.playGlobalFootstepSound(sound.getName(), x, y, z, volume, pitch);
			}
		}
	}
	
	private static final class ScheduledSound {
		private final String name;
		private final double x;
		private final double y;
		private final double z;
		private final float volume;
		private final float pitch;
		
		private final int delay;
		private final long startTime;
		
		public ScheduledSound(String name, double x, double y, double z, float volume, float pitch, int delay) {
			this.name = name;
			this.x = x;
			this.y = y;
			this.z = z;
			this.volume = volume;
			this.pitch = pitch;
			this.delay = delay;
			this.startTime = System.currentTimeMillis();
		}
	}
}
