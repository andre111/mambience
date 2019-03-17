package me.andre111.mambience.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerLeaveCallback {
	public static final Event<PlayerLeaveCallback> EVENT = EventFactory.createArrayBacked(PlayerLeaveCallback.class,
			(listeners) -> {
				if (EventFactory.isProfilingEnabled()) {
					return (serverPlayerEntity) -> {
						serverPlayerEntity.world.getProfiler().push("fabricPlayerLeave");
						for (PlayerLeaveCallback event : listeners) {
							serverPlayerEntity.world.getProfiler().push(EventFactory.getHandlerName(event));
							event.onPlayerLeave(serverPlayerEntity);
							serverPlayerEntity.world.getProfiler().pop();
						}
						serverPlayerEntity.world.getProfiler().pop();
					};
				} else {
					return (serverPlayerEntity) -> {
						for (PlayerLeaveCallback event : listeners) {
							event.onPlayerLeave(serverPlayerEntity);
						}
					};
				}
			}
	);

	void onPlayerLeave(ServerPlayerEntity serverPlayerEntity);
}
