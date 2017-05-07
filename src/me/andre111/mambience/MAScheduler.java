package me.andre111.mambience;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import me.andre111.mambience.player.MAPlayer;
import me.andre111.mambience.scan.BlockScanner;
import me.andre111.mambience.script.Variables;
import me.andre111.mambience.sound.Soundscapes;

public abstract class MAScheduler implements Runnable {
	private MALogger logger;
	private int intervall;
	
	private long timer;
	private ArrayList<MAPlayer> players = new ArrayList<MAPlayer>();
	private Queue<BlockScanner> scannerQueue = new LinkedList<BlockScanner>();
	
	public MAScheduler(MALogger l, int i) {
		logger = l;
		intervall = i;
		
		timer = 0;
	}
	
	public void addPlayer(MAPlayer maplayer) {
		players.add(maplayer);
		Variables.init(maplayer);
		Soundscapes.init(maplayer);
	}
	
	public void removePlayer(UUID player) {
		MAPlayer toRemove = null;
		for(MAPlayer maplayer : players) {
			if(maplayer.getPlayerUUID().equals(player)) {
				toRemove = maplayer;
				break;
			}
		}
		
		if(toRemove==null) {
			logger.log(player+" had no BlockScanner associated with them!");
		} else {
			players.remove(toRemove);
			scannerQueue.remove(toRemove.getScanner());
		}
	}
	
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		long varTime = startTime;
		long scapeTime = startTime;
		timer++;
		
		//update players
		for(MAPlayer maplayer : players) {
			Variables.update(maplayer);
		}
		varTime = System.currentTimeMillis();
		for(MAPlayer maplayer : players) {
			Soundscapes.update(maplayer);
		}
		scapeTime = System.currentTimeMillis();
		
		//search for Scanners needing refreshing
		for(MAPlayer maplayer : players) {
			if(maplayer.getScanner().getLastScan()+intervall<=timer) {
				if(!scannerQueue.contains(maplayer.getScanner())) {
					scannerQueue.add(maplayer.getScanner());
				}
			}
		}
		
		//refresh 
		int refreshed = 0;
		int perTick = (int) Math.max(1, Math.ceil(getPlayerCount() / ((double) intervall)*20));
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
			logger.log("\tVar: "+(varTime-startTime)+"ms     Scape: "+(scapeTime-varTime)+"ms     Scanner: "+(endTime-scapeTime)+"ms!");
		//}
	}

	public abstract int getPlayerCount();
}
