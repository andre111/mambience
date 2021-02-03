/*
 * Copyright (c) 2020 André Schweiger
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
package me.andre111.mambience;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import me.andre111.mambience.accessor.Accessor;
import me.andre111.mambience.effect.Effects;
import me.andre111.mambience.scan.BlockScanner;
import me.andre111.mambience.sound.Sounds;

public class MAScheduler {
	private MALogger logger;
	private int interval;
	
	private long timer;
	private Set<MAPlayer> players = new HashSet<>();
	private Queue<BlockScanner> scannerQueue = new LinkedList<>();

	private boolean clearPlayers = false;
	private List<MAPlayer> newPlayers = new ArrayList<>();
	
	public MAScheduler(MALogger l, int i) {
		logger = l;
		interval = i;
		
		timer = 0;
	}
	
	public void addPlayer(UUID player, Accessor accessor, MALogger logger) {
		MAPlayer maplayer = new MAPlayer(player, accessor, logger);
		synchronized(newPlayers) {
			newPlayers.add(maplayer);
		}
	}
	
	public void clearPlayers() {
		clearPlayers = true;
	}
	
	public void runSyncUpdate() {
		long startTime = System.currentTimeMillis();
		long variableTime = startTime;
		timer++;
		
		// clear or add players
		synchronized(newPlayers) {
			if(clearPlayers) {
				players.clear();
				clearPlayers = false;
			}
			
			players.addAll(newPlayers);
			newPlayers.clear();
		}
		
		// update players
		Iterator<MAPlayer> iterator = players.iterator();
		while(iterator.hasNext()) {
			MAPlayer maplayer = iterator.next();
			
			// remove players
			if(!maplayer.getAccessor().updatePlayerInstance()) {
				iterator.remove();
				continue;
			}
			
			// update variables
			maplayer.getVariables().update();
			
			// update footsteps
			maplayer.getFootsteps().update();
			
			// enqueue scanners requiring update
			if(maplayer.getScanner().getLastScan()+interval<=timer) {
				if(!scannerQueue.contains(maplayer.getScanner())) {
					scannerQueue.add(maplayer.getScanner());
				}
			}
			
			// update "last tick" values
			maplayer.getAccessor().updateLastPosition();
		}
		variableTime = System.currentTimeMillis();
		
		// update scanners 
		int refreshed = 0;
		int perTick = (int) Math.max(1, Math.ceil(players.size() / ((double) interval) * 1.5));
		for(int i=0; i<perTick; i++) {
			BlockScanner scanner = scannerQueue.poll();
			if(scanner!=null) {
				scanner.performScan();
				scanner.setLastScan(timer);
				refreshed++;
			}
		}
		long scannerTime = System.currentTimeMillis();
		
		// update effects
		Effects.tick();
		
		long endTime = System.currentTimeMillis();
		if(timer % 20 == 0) {
			logger.log("Refreshing "+refreshed+"/"+players.size()+" Player(s) last tick took "+(endTime-startTime)+"ms!");
			logger.log("\tPlayers: "+(variableTime-startTime)+"ms      Scanners: "+(scannerTime-variableTime)+"ms      Effects: "+(scannerTime-endTime)+"ms!");
		}
	}
	
	public void runAsyncUpdate() {
		long startTime = System.currentTimeMillis();
		
		// create "unmodified" list of players
		List<MAPlayer> toUpdate;
		synchronized(newPlayers) {
			toUpdate = new ArrayList<>(players);
		}

		// update soundscapes
		for(MAPlayer maplayer : toUpdate) {
			Sounds.update(maplayer);
		}
		long soundTime = System.currentTimeMillis();
		
		// update effects
		for(MAPlayer maplayer : toUpdate) {
			Effects.update(maplayer);
		}
		
		
		long endTime = System.currentTimeMillis();
		logger.log("Soundscape update took "+(soundTime-startTime)+"ms - Effect update took "+(endTime-soundTime)+"ms!");
	}
}
