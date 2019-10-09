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
import java.util.UUID;

import me.andre111.mambience.accessor.Accessor;
import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.script.MAScriptEngine;
import me.andre111.mambience.script.Variables;
import me.andre111.mambience.sound.Soundscapes;

public class MAmbience {
	private static MALogger logger;
	private static MAScheduler scheduler;
	
	public static void init(MALogger malogger, File configPath) {
		MAmbience.logger = malogger;
		EngineConfig.initialize(logger, configPath);
		
		MAScriptEngine.createScriptEngine(logger);
		Variables.init();
		
		Soundscapes.initGlobal();

		scheduler = new MAScheduler(logger, 1);
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
