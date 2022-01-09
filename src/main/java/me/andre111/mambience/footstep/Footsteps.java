/*
 * Copyright (c) 2021 Andre Schweiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.andre111.mambience.footstep;

import java.util.List;

import me.andre111.mambience.MAPlayer;
import me.andre111.mambience.accessor.Accessor;
import me.andre111.mambience.config.FootstepLoader;
import me.andre111.mambience.sound.Sound;

public class Footsteps {	
	private final MAPlayer player;
	private final Accessor accessor;
	
	private double accumulatedDistance;
	private boolean wasPracticallyStanding;
	private boolean isInAir;
	private double fallDistance;
	
	private boolean rightFoot;
	
	public Footsteps(MAPlayer player) {
		this.player = player;
		this.accessor = player.getAccessor();
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
	}
	
	private void updateSteps(double speed, double distance) {
		//TODO...
		if(accessor.isSneaking() || accessor.isSubmerged() || !accessor.isOnGround()) return;
		accumulatedDistance += speed;
		
		// play "wander" sound when the player has just stopped moving -> "break sound"
		boolean isPracticallyStanding = speed < 0.01;
		if(isPracticallyStanding && !wasPracticallyStanding) {
			playEvent(FSEvent.WANDER, false, -1/16.0);
		}
		wasPracticallyStanding = isPracticallyStanding;
		
		// determine stride distance and event (TODO: configurable values)
		double strideDistance = 1.5;
		FSEvent event = speed >= 0.22f ? FSEvent.RUN : FSEvent.WALK;
		//TODO: check for other situations (i.e. on ladder or moving on steps
		
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
		List<FSMaterial> materials = FootstepLoader.BLOCK_MAP.get(accessor.getBlock(footBlockX, footBlockY, footBlockZ));
		if(materials == null || materials.isEmpty()) materials = FootstepLoader.BLOCK_MAP.get(accessor.getBlock(footBlockX, footBlockY-1, footBlockZ));
		if(materials == null) return;

		playSounds(event, footX, footY, footZ, materials);
		
		// determine armor materials
		List<FSMaterial> armorMaterials = FootstepLoader.ARMOR_MAP.get(accessor.getArmor(2));
		if(armorMaterials == null || armorMaterials.isEmpty()) armorMaterials = FootstepLoader.ARMOR_MAP.get(accessor.getArmor(1));
		List<FSMaterial> feetMaterials = FootstepLoader.ARMOR_MAP.get(accessor.getArmor(0));
		
		if(armorMaterials != null) {
			playSounds(event, footX, footY, footZ, armorMaterials);
		}
		if(feetMaterials != null && !feetMaterials.equals(armorMaterials)) {
			playSounds(event, footX, footY, footZ, feetMaterials);
		}
	}

	private void playSounds(FSEvent event, double x, double y, double z, List<FSMaterial> materials) {
		for(FSMaterial material : materials) {
			playSounds(event, x, y, z, material);
		}
	}
	
	private void playSounds(FSEvent event, double x, double y, double z, FSMaterial material) {
		// determine sounds
		Sound[] sounds = null;
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
		for(Sound sound : sounds) {
			player.getSoundPlayer().playSound(sound, x, y, z, true);
		}
	}
}
