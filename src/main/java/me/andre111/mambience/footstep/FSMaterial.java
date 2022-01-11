/*
 * Copyright (c) 2022 Andre Schweiger
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

import me.andre111.mambience.sound.Sound;

public class FSMaterial {
	private final String id;
	private final Sound[] wanderSounds;
	private final Sound[] walkSounds;
	private final Sound[] runSounds;
	private final Sound[] jumpSounds;
	private final Sound[] landSounds;
	
	public FSMaterial(String id, Sound[] wanderSounds, Sound[] walkSounds, Sound[] runSounds, Sound[] jumpSounds, Sound[] landSounds) {
		this.id = id;
		this.wanderSounds = wanderSounds;
		this.walkSounds = walkSounds;
		this.runSounds = runSounds;
		this.jumpSounds = jumpSounds;
		this.landSounds = landSounds;
	}
	
	public String getID() {
		return id;
	}

	public Sound[] getWanderSounds() {
		return wanderSounds;
	}

	public Sound[] getWalkSounds() {
		return walkSounds;
	}

	public Sound[] getRunSounds() {
		return runSounds;
	}

	public Sound[] getJumpSounds() {
		return jumpSounds;
	}

	public Sound[] getLandSounds() {
		return landSounds;
	}
}
