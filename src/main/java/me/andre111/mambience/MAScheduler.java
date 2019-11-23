/*
 * Copyright (c) 2019 André Schweiger
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
import me.andre111.mambience.player.MAPlayer;
import me.andre111.mambience.scan.BlockScanner;
import me.andre111.mambience.script.Variables;
import me.andre111.mambience.sound.Soundscapes;

public class MAScheduler implements Runnable {
	private MALogger logger;
	private int intervall;
	
	private long timer;
	private Set<MAPlayer> players = new HashSet<>();
	private Queue<BlockScanner> scannerQueue = new LinkedList<>();

	private List<MAPlayer> newPlayers = new ArrayList<>();
	
	public MAScheduler(MALogger l, int i) {
		logger = l;
		intervall = i;
		
		timer = 0;
	}
	
	public void addPlayer(UUID player, Accessor accessor, MALogger logger) {
		MAPlayer maplayer = new MAPlayer(player, accessor, logger);
		Soundscapes.init(maplayer);
		synchronized(newPlayers) {
			newPlayers.add(maplayer);
		}
	}
	
	public void clearPlayers() {
		players.clear();
	}
	
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		long scapeTime = startTime;
		timer++;
		
		// add players
		synchronized(newPlayers) {
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
			
			Variables.update(maplayer);
			Soundscapes.update(maplayer);
			
			if(maplayer.getScanner().getLastScan()+intervall<=timer) {
				if(!scannerQueue.contains(maplayer.getScanner())) {
					scannerQueue.add(maplayer.getScanner());
				}
			}
		}
		scapeTime = System.currentTimeMillis();
		
		//refresh 
		int refreshed = 0;
		int perTick = (int) Math.max(1, Math.ceil(players.size() / ((double) intervall)*20));
		for(int i=0; i<perTick; i++) {
			BlockScanner scanner = scannerQueue.poll();
			if(scanner!=null) {
				scanner.performScan();
				scanner.setLastScan(timer);
				refreshed++;
			}
		}
		
		long endTime = System.currentTimeMillis();
		//if(timer%20==0) {
			logger.log("Refreshing "+refreshed+" Player(s) took "+(endTime-startTime)+"ms!");
			logger.log("\tVar+Scape: "+(scapeTime-startTime)+"ms     Scanner: "+(endTime-scapeTime)+"ms!");
		//}
	}
}
