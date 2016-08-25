package me.andre111.mambience;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import me.andre111.mambience.scan.BlockScanner;
import me.andre111.mambience.script.Variables;
import me.andre111.mambience.sound.Soundscapes;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MAScheduler implements Runnable, Listener {
	private MAmbience plugin;
	private int intervall;
	
	private long timer;
	private ArrayList<MAPlayer> players = new ArrayList<MAPlayer>();
	private Queue<BlockScanner> scannerQueue = new LinkedList<BlockScanner>();
	
	public MAScheduler(MAmbience p, int i) {
		plugin = p;
		intervall = i;
		
		timer = 0;
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 20, 20);
		//Bukkit.getScheduler().scheduleSyncRepeatingTask(p, this, 1, 1);
		Bukkit.getPluginManager().registerEvents(this, p);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		MAPlayer maplayer = new MAPlayer(plugin, event.getPlayer());
		players.add(maplayer);
		Variables.init(maplayer);
		Soundscapes.init(maplayer);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		MAPlayer toRemove = null;
		for(MAPlayer maplayer : players) {
			if(maplayer.getPlayerUUID().equals(event.getPlayer().getUniqueId())) {
				toRemove = maplayer;
				break;
			}
		}
		
		if(toRemove==null) {
			plugin.log(event.getPlayer().getName()+" had no BlockScanner associated with them!");
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
		int perTick = (int) Math.max(1, Math.ceil(Bukkit.getOnlinePlayers().size() / ((double) intervall)*20));
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
			plugin.log("Refreshing "+refreshed+" Player(s) took "+(endTime-startTime)+"ms!");
			plugin.log("\tVar: "+(varTime-startTime)+"ms     Scape: "+(scapeTime-varTime)+"ms     Scanner: "+(endTime-scapeTime)+"ms!");
		//}
	}

}
