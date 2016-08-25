package me.andre111.mambience.script;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import me.andre111.mambience.MAmbience;

public class MAScriptEngine {
	private MAmbience plugin;
	private ScriptEngine scriptEngine;
	
	protected MAScriptEngine(MAmbience p, ScriptEngine se) {
		plugin = p;
		scriptEngine = se;
	}
	
	public Object evalJS(String js) {
        try {
            return scriptEngine.eval(js);
        } catch (ScriptException ex) {
            plugin.error("Script failed\n" + js + "\n" + ex.getMessage());
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
			plugin.error("Script compilation failed\n" + js + "\n" + ex.getMessage());
		}
		
		return null;
	}
	public Object invokeFunction(String name) {
		Invocable invocable = (Invocable) scriptEngine;
		try {
			return invocable.invokeFunction(name);
		} catch (NoSuchMethodException | ScriptException ex) {
			plugin.error("Function invoke failed\n" + name + "\n" + ex.getMessage());
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
