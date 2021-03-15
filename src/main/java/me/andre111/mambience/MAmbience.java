/*
 * Copyright (c) 2021 André Schweiger
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
import java.util.UUID;

import me.andre111.mambience.accessor.Accessor;
import me.andre111.mambience.config.Config;

//TODO: adjust sounds, get rid of some repetitiveness
public class MAmbience {
	private static MALogger logger;
	private static MAScheduler scheduler;
	
	public static void init(MALogger malogger, File configPath) {
		MAmbience.logger = malogger;
		Config.initialize(logger, configPath);

		scheduler = new MAScheduler(logger);
	}
	
	public static MALogger getLogger() {
		return logger;
	}
	
	public static MAScheduler getScheduler() {
		return scheduler;
	}
	
	public static void addPlayer(UUID player, Accessor accessor) {
		scheduler.addPlayer(player, accessor, logger);
	}
}
