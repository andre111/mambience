package me.andre111.mambience;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.fabric.event.PlayerJoinCallback;
import me.andre111.mambience.fabric.event.PlayerLeaveCallback;
import me.andre111.mambience.player.MAPlayerFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.minecraft.server.MinecraftServer;

public class MAmbienceFabric implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static MinecraftServer server;
	public static MAmbienceFabric instance;
	
	private MALogger logger;
	private MAScheduler scheduler;
	
	private int ticker;
	private long lastTick;
	
	@Override
	public void onInitialize() {
		instance = this;
		logger = new MALogger() {
			@Override
			public void log(String s) {
				if(EngineConfig.DEBUGLOGGING) {
					LOGGER.info(s);
				}
			}

			@Override
			public void error(String s) {
				LOGGER.error(s);
			}
		};
		
		ServerStartCallback.EVENT.register(server -> MAmbienceFabric.server = server);

		EngineConfig.initialize(logger, new File("./config/mambience"));
		scheduler = new MAScheduler(logger, 1) {
			@Override
			public int getPlayerCount() {
				return server==null ? 0 : server.getCurrentPlayerCount();
			}
		};
		WorldTickCallback.EVENT.register(world -> {
			// only run on server
			if(world.isClient() || server == null) {
				return;
			}
			
			// only run when not trying to catch up
			if(System.currentTimeMillis()-lastTick < 1000 / 20 / 2) {
				return;
			}
			lastTick = System.currentTimeMillis();
			
			// update
			ticker++;
			if(ticker == 20) {
				ticker = 0;
				scheduler.run();
			}
		});
		PlayerJoinCallback.EVENT.register((connection, player) -> {
			scheduler.addPlayer(new MAPlayerFabric(player, logger));
		});
		PlayerLeaveCallback.EVENT.register(player -> {
			scheduler.removePlayer(player.getUuid());
		});
	}
}
