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

import me.andre111.mambience.config.Config;

public class MALogger {
	private StringReciever logReciever;
	private StringReciever errorReciever;
	
	public MALogger(StringReciever logReciever, StringReciever errorReciever) {
		this.logReciever = logReciever;
		this.errorReciever = errorReciever;
	}
	
	public void log(String s) {
		if(Config.debugLogging()) {
			logReciever.recieveString(s);
		}
	}
	
	public void error(String s) {
		errorReciever.recieveString(s);
	}
	
	public static interface StringReciever {
		public void recieveString(String s);
	}
}
