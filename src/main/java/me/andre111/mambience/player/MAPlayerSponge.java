package me.andre111.mambience.player;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.scan.BlockScannerSponge;
import me.andre111.mambience.script.VariablesSponge;

public class MAPlayerSponge extends MAPlayer {
	public MAPlayerSponge(Player p, MALogger logger) {
		super(p.getUniqueId(), new BlockScannerSponge(p, EngineConfig.SIZEX, EngineConfig.SIZEY, EngineConfig.SIZEZ), new VariablesSponge(), logger);
	}

	@Override
	public void playSound(String sound, float volume, float pitch) {
		Optional<Player> optPlayer = Sponge.getServer().getPlayer(playerUUID);
		if(optPlayer.isPresent()) {
			optPlayer.get().playSound(SoundType.builder().build(sound), SoundCategories.AMBIENT, optPlayer.get().getLocation().getPosition(), volume, pitch);
		}
	}

	@Override
	public void stopSound(String sound) {
		// TODO Auto-generated method stub
		Optional<Player> optPlayer = Sponge.getServer().getPlayer(playerUUID);
		if(optPlayer.isPresent()) {
			optPlayer.get().stopSounds(SoundType.builder().build(sound), SoundCategories.AMBIENT);
		}
	}
}
