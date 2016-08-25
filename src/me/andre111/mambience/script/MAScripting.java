package me.andre111.mambience.script;

import java.util.ArrayList;

import javax.script.ScriptEngineManager;

import me.andre111.mambience.MAmbience;

public class MAScripting {
	private static ArrayList<Macro> macros = new ArrayList<Macro>();
	
	public static MAScriptEngine newScriptEngine(MAmbience p) {
		ScriptEngineManager man = new ScriptEngineManager(null);
        return new MAScriptEngine(p, man.getEngineByName("JavaScript"));
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
