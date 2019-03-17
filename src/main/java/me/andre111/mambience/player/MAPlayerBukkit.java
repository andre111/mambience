package me.andre111.mambience.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.scan.BlockScannerBukkit;
import me.andre111.mambience.script.VariablesBukkit;

public class MAPlayerBukkit extends MAPlayer {
	public MAPlayerBukkit(Player p, MALogger logger) {
		super(p.getUniqueId(), new BlockScannerBukkit(p, EngineConfig.SIZEX, EngineConfig.SIZEY, EngineConfig.SIZEZ), new VariablesBukkit(), logger);
	}

	@Override
	public void playSound(String sound, float volume, float pitch) {
		Player player = Bukkit.getPlayer(playerUUID);
		Location location = player.getLocation();
		
		player.playSound(location, sound, volume, pitch);
	}

	@Override
	public void stopSound(String sound) {
		Player player = Bukkit.getPlayer(playerUUID);
		
		player.stopSound(sound);
	}
}
