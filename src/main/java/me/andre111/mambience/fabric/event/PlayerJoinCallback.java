package me.andre111.mambience.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerJoinCallback {
	public static final Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class,
			(listeners) -> {
				if (EventFactory.isProfilingEnabled()) {
					return (clientConnection, serverPlayerEntity) -> {
						serverPlayerEntity.world.getProfiler().push("fabricPlayerJoin");
						for (PlayerJoinCallback event : listeners) {
							serverPlayerEntity.world.getProfiler().push(EventFactory.getHandlerName(event));
							event.onPlayerJoin(clientConnection, serverPlayerEntity);
							serverPlayerEntity.world.getProfiler().pop();
						}
						serverPlayerEntity.world.getProfiler().pop();
					};
				} else {
					return (clientConnection, serverPlayerEntity) -> {
						for (PlayerJoinCallback event : listeners) {
							event.onPlayerJoin(clientConnection, serverPlayerEntity);
						}
					};
				}
			}
	);

	void onPlayerJoin(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity);
}
