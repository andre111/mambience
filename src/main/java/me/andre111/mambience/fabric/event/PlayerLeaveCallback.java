/*
 * Copyright (c) 2020 André Schweiger
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
