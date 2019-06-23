package me.andre111.mambience.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.script.MAScripting;
import me.andre111.mambience.script.Variables;

public class EngineConfig {
	public static int SIZEX = 32;
	public static int SIZEY = 16;
	public static int SIZEZ = 32;
	public static boolean STOPSOUNDS = false;
	public static boolean DEBUGLOGGING = false;
	
	public static void initialize(MALogger logger, File configRoot) {
		exportSettings(configRoot);
		JsonParser parser = new JsonParser();
		
		File engine = new File(configRoot, "/settings/engine.json");
		try {
			JsonObject engineElement = parser.parse(new FileReader(engine)).getAsJsonObject();
			
			loadSettings(engineElement.get("Settings").getAsJsonObject());
			loadVariables(engineElement.get("Variables").getAsJsonArray());
			loadMacros(engineElement.get("Macros").getAsJsonArray());
			loadSoundscapes(logger, configRoot, engineElement.get("Soundscapes").getAsJsonArray());
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			logger.error("Exception reading engine config: "+e);
		}
	}
	
	private static void exportSettings(File folder) {
		if(!folder.exists()) {
			folder.mkdir();
		}
		
		exportSingleFile(folder, "/settings/engine.json");
		
		exportSingleFile(folder, "/settings/soundscapes/xshared.json");
		exportSingleFile(folder, "/settings/soundscapes/cave.json");
		exportSingleFile(folder, "/settings/soundscapes/desert.json");
		exportSingleFile(folder, "/settings/soundscapes/end.json");
		exportSingleFile(folder, "/settings/soundscapes/forest.json");
		exportSingleFile(folder, "/settings/soundscapes/hell.json");
		exportSingleFile(folder, "/settings/soundscapes/jungle.json");
		exportSingleFile(folder, "/settings/soundscapes/lava.json");
		exportSingleFile(folder, "/settings/soundscapes/mountain.json");
		exportSingleFile(folder, "/settings/soundscapes/plains.json");
		exportSingleFile(folder, "/settings/soundscapes/rain.json");
		exportSingleFile(folder, "/settings/soundscapes/seaside.json");
		exportSingleFile(folder, "/settings/soundscapes/snow.json");
		exportSingleFile(folder, "/settings/soundscapes/swamp.json");
	}
	private static void exportSingleFile(File folder, String path) {
		File file = new File(folder, path);
		if(!file.exists()) {
			try {
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				Files.copy(EngineConfig.class.getResourceAsStream(path), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void loadSettings(JsonObject element) {
		SIZEX = element.get("ScannnerX").getAsInt();
		SIZEY = element.get("ScannnerY").getAsInt();
		SIZEZ = element.get("ScannnerZ").getAsInt();
		STOPSOUNDS = element.get("StopSounds").getAsBoolean();
		if(element.has("DebugLogging")) {
			DEBUGLOGGING = element.get("DebugLogging").getAsBoolean();
		}
	}
	
	private static void loadSoundscapes(MALogger logger, File configRoot, JsonArray array) {
		for(int i=0; i<array.size(); i++) {
			String name = array.get(i).getAsString();
			SoundscapeConfig.loadSoundscape(logger, new File(configRoot, "/settings/"+name));
		}
	}
	
	private static void loadVariables(JsonArray array) {
		for(int i=0; i<array.size(); i++) {
			JsonObject variable = array.get(i).getAsJsonObject();
			
			String name = variable.get("Name").getAsString();
			String type = variable.get("Type").getAsString();
			switch(type) {
			case "BlockCount": {
				JsonArray matArray = variable.get("Materials").getAsJsonArray();
				String[] blocks = new String[matArray.size()];
				for(int j=0; j<matArray.size(); j++) {
					blocks[j] = matArray.get(j).getAsString();
				}
				Variables.addBlockCountVariable(name, blocks);
				break;
			}
			case "BiomeCount": {
				JsonArray biomeArray = variable.get("Biomes").getAsJsonArray();
				String[] biomes = new String[biomeArray.size()];
				for(int j=0; j<biomeArray.size(); j++) {
					biomes[j] = biomeArray.get(j).getAsString();
				}
				Variables.addBiomeCountVariable(name, biomes);
				break;
			}
			}
		}
	}
	
	private static void loadMacros(JsonArray array) {
		for(int i=0; i<array.size(); i++) {
			JsonObject macro = array.get(i).getAsJsonObject();
			
			String name = macro.get("Name").getAsString();
			String code = macro.get("Code").getAsString();
			MAScripting.addMacro(name, code);
		}
	}
}
