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
package me.andre111.mambience;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.Unpooled;
import me.andre111.mambience.accessor.AccessorFabricClient;
import me.andre111.mambience.accessor.AccessorFabricServer;
import me.andre111.mambience.fabric.MAmbienceResourceReloadListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MAmbienceFabric implements ModInitializer, ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Identifier CHANNEL = new Identifier("mambience", "server");

	public static MinecraftServer server;
	public static MAmbienceFabric instance;

	private int ticker;
	private long lastTick;

	private boolean runClientSide;
	private boolean serverPresent;

	@Override
	public void onInitialize() {
		initCommon();
		initServer();
	}

	@Override
	public void onInitializeClient() {
		initCommon();
		initClient();
	}

	// note: call from both initialize methods, because fabric creates two separate instances
	private void initCommon() {
		instance = this;
		MAmbience.init(new MALogger(LOGGER::info, LOGGER::error), new File("./config/mambience"));
	}

	private void initServer() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new MAmbienceResourceReloadListener());
		
		// run server side processing
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			MAmbienceFabric.server = server;
			tick();
		});

		// Client Connect event
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();

			// register player
			MAmbience.addPlayer(player.getUuid(), new AccessorFabricServer(player.getUuid()));
		});
		
		// client registered channel -> mod is present on client side
		S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> {
			// -> send notify payload of server side presence (mambience:server channel with "enabled" message)
			if(channels.contains(CHANNEL)) {
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeBytes("enabled".getBytes());
				ServerPlayNetworking.send(handler.getPlayer(), CHANNEL, buf);
			}
		});
	}

	private void initClient() {
		// run client side processing
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// disable client side ambient sounds when not in game
			if(client.world == null || client.player == null) {
				if(runClientSide) {
					MAmbience.getScheduler().clearPlayers();
					runClientSide = false;
				}
			}

			if(runClientSide) {
				tick();
			}
		});
		
		// World Start / Server Connect and Mod Presence events
		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			// reset server mod presence state
			serverPresent = false;
		});
		ClientPlayConnectionEvents.JOIN.register((handlder, sender, client) -> {
			// enable client side processing (only when not on the integrated server and the server did not report mod presence)
			if(!runClientSide && !client.isIntegratedServerRunning() && !serverPresent) {
				MAmbience.getLogger().log("enabling client side processing");
				MAmbience.addPlayer(client.player.getUuid(), new AccessorFabricClient(client.player.getUuid()));
				runClientSide = true;
			}
		});
		// this also automatically causes the fabric API to register the channel at the server thus notifying it of client side mod presence
		ClientPlayNetworking.registerGlobalReceiver(CHANNEL, (client, handler, buf, responseSender) -> {
			// server has mod presence -> disable client side processing
			MAmbience.getLogger().log("server reported MAmbience present: disabled client side processing");
			serverPresent = true;
			if(runClientSide) {
				MAmbience.getScheduler().clearPlayers();
				runClientSide = false;
			}
		});
	}

	private void tick() {
		// only run when not trying to catch up
		if(System.currentTimeMillis()-lastTick < 1000 / 20 / 2) {
			return;
		}
		lastTick = System.currentTimeMillis();

		// update
		MAmbience.getScheduler().runSyncUpdate();
		ticker++;
		if(ticker == 20) {
			ticker = 0;
			MAmbience.getScheduler().runAsyncUpdate(); //TODO: make this async
		}
	}
}
