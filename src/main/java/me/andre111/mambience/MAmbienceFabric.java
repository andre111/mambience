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

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.Unpooled;
import me.andre111.mambience.accessor.AccessorFabricClient;
import me.andre111.mambience.accessor.AccessorFabricServer;
import me.andre111.mambience.fabric.event.PlayerJoinCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class MAmbienceFabric implements ModInitializer, ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	public static MinecraftServer server;
	public static MAmbienceFabric instance;

	private int ticker;
	private long lastTick;

	private boolean runClientSide;

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
		ServerTickCallback.EVENT.register(server -> {
			MAmbienceFabric.server = server;
			tick();
		});

		PlayerJoinCallback.EVENT.register((connection, player) -> {
			// send notify payload (mambience:server channel with "enabled" message)
			// TODO: this currently ignores the registered state of the channel, but that is not too important
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeBytes("enabled".getBytes());
			player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier("mambience", "server"), buf));

			// register player
			MAmbience.addPlayer(player.getUuid(), new AccessorFabricServer(player.getUuid()));
		});
	}

	private void initClient() {
		ClientTickCallback.EVENT.register(client -> {
			// disable client side ambient sounds when not in game
			if(client.isIntegratedServerRunning() || client.world == null || client.player == null) {
				if(runClientSide) {
					MAmbience.getLogger().log("automatically disabled client side ambient sounds");
					MAmbience.getScheduler().clearPlayers();
					runClientSide = false;
				}
			}

			if(runClientSide) {
				tick();
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
		ticker++;
		if(ticker == 20) {
			ticker = 0;
			MAmbience.getScheduler().run();
		}
	}

	// enable or disable client side ambient sounds dependent on server support
	public void onStartGameSession(MinecraftClient client) {
		if(!runClientSide && !client.isIntegratedServerRunning()) {
			MAmbience.getLogger().log("enabling client side ambient sounds");
			MAmbience.addPlayer(client.player.getUuid(), new AccessorFabricClient(client.player.getUuid()));
			runClientSide = true;

			// notify server of our presence by registering plugin channel
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeBytes("mambience:server".getBytes());
			buf.writeByte(0);
			client.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(new Identifier("minecraft", "register"), buf));
		}
	}

	public void onServerMAmbiencePresent() {
		if(runClientSide) {
			MAmbience.getLogger().log("server reported MAmbience present: disabled client side ambient sounds");
			MAmbience.getScheduler().clearPlayers();
			runClientSide = false;
		}
	}
}
