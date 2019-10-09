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

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import me.andre111.mambience.MALogger;

public class MAScriptEngine {
	private static MAScriptEngine INSTANCE;
	public static void createScriptEngine(MALogger logger) {
		ScriptEngineManager man = new ScriptEngineManager(null);
		INSTANCE = new MAScriptEngine(logger, man.getEngineByName("JavaScript"));
	}
	public static MAScriptEngine getInstance() {
		return INSTANCE;
	}
	
	private MALogger logger;
	private ScriptEngine scriptEngine;
	
	protected MAScriptEngine(MALogger l, ScriptEngine se) {
		logger = l;
		scriptEngine = se;
	}
	
	public Object evalJS(String js) {
        try {
            return scriptEngine.eval(js);
        } catch (ScriptException ex) {
        	logger.error("Script failed\n" + js + "\n" + ex.getMessage());
        }
        
        return null;
    }
	
	public CompiledScript compileScript(String js) {
		Compilable compilingEngine = (Compilable) scriptEngine;
		try {
			CompiledScript script = compilingEngine.compile(js);
			script.eval(scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE));
			return script;
		} catch (ScriptException ex) {
			logger.error("Script compilation failed\n" + js + "\n" + ex.getMessage());
		}
		
		return null;
	}
	public Object invokeFunction(String name) {
		Invocable invocable = (Invocable) scriptEngine;
		try {
			return invocable.invokeFunction(name);
		} catch (NoSuchMethodException | ScriptException ex) {
			logger.error("Function invoke failed\n" + name + "\n" + ex.getMessage());
		}
		
		return null;
	}
	
	public Bindings getEngineScopeBindings() {
		return scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
	}
	
	public void setValue(String name, Object value) {
		scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).put(name, value);
	}
}
