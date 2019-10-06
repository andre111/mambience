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
package me.andre111.mambience.script;

import java.util.ArrayList;

import javax.script.ScriptEngineManager;

import me.andre111.mambience.MALogger;

public class MAScripting {
	private static ArrayList<Macro> macros = new ArrayList<Macro>();
	
	public static MAScriptEngine newScriptEngine(MALogger logger) {
		ScriptEngineManager man = new ScriptEngineManager(null);
        return new MAScriptEngine(logger, man.getEngineByName("JavaScript"));
	}
	
	public static String expandMacros(String js) {
		for(Macro macro : macros) {
    		js = macro.expand(js);
    	}
		
        return js;
    }
	
	public static void addMacro(String name, String content) {
		Macro macro = new Macro();
		macro.name = "#" + name + "#";
		macro.content = content;
		macros.add(macro);
	}
	
	private static class Macro {
		private String name;
		private String content;
		
		public String expand(String js) {
			if(!js.contains(name)) return js;
			
			//TODO: This can cause infinite loops with bad macro definitions
			String currentContent = content;
			if(currentContent.contains("#")) {
				for(Macro macro : macros) {
					if(macro.name.equals(this.name)) continue;
					
					currentContent = macro.expand(currentContent);
	        	}
			}
			
			js = js.replace(name, currentContent);
			
			return js;
		}
	}
}
