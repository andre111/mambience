package me.andre111.mambience.player;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.MAmbienceFabric;
import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.scan.BlockScannerFabric;
import me.andre111.mambience.script.VariablesFabric;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class MAPlayerFabric extends MAPlayer {
	public MAPlayerFabric(ServerPlayerEntity p, MALogger logger) {
		super(p.getUuid(), new BlockScannerFabric(p, EngineConfig.SIZEX, EngineConfig.SIZEY, EngineConfig.SIZEZ), new VariablesFabric(), logger);
	}

	@Override
	public void playSound(String sound, float volume, float pitch) {
		ServerPlayerEntity player = MAmbienceFabric.server.getPlayerManager().getPlayer(playerUUID);
		
		//SoundEvent event = new SoundEvent(new Identifier(sound));
		//player.playSound(event, SoundCategory.AMBIENT, volume, pitch); // only works for registered sound events -> only when client has mod installed
		// also only works for registered sound events -> only when client has mod installed, but will bind sounds to player (no longer positional)
		//player.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(event, SoundCategory.AMBIENT, player, volume, pitch));
		
		player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(new Identifier(sound), SoundCategory.AMBIENT, player.getPosVector(), volume, pitch));
	}

	@Override
	public void stopSound(String sound) {
		ServerPlayerEntity player = MAmbienceFabric.server.getPlayerManager().getPlayer(playerUUID);

		player.networkHandler.sendPacket(new StopSoundS2CPacket(new Identifier(sound), SoundCategory.AMBIENT));
	}
}
