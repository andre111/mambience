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
		
		exportSingleFile(folder, "/settings/soundscapes/ambiotic/background.json");
		exportSingleFile(folder, "/settings/soundscapes/ambiotic/beach.json");
		exportSingleFile(folder, "/settings/soundscapes/ambiotic/coldforest.json");
		exportSingleFile(folder, "/settings/soundscapes/ambiotic/forest.json");
		exportSingleFile(folder, "/settings/soundscapes/ambiotic/jungle.json");
		exportSingleFile(folder, "/settings/soundscapes/ambiotic/plains.json");
		exportSingleFile(folder, "/settings/soundscapes/ambiotic/savanna.json");
		exportSingleFile(folder, "/settings/soundscapes/ambiotic/swamp.json");
		exportSingleFile(folder, "/settings/soundscapes/ambiotic/water.json");
		
		//TODO: Replace most (longer) sounds from RP with sounds from MSI (MAtmos) as well as extending
		//C:\Users\Andr�\Downloads\Minecraft Sound Improvement 1.40\matmos\packs\matmos_default\assets\minecraft\sound\matmos_MSI
		exportSingleFile(folder, "/settings/soundscapes/msi/xshared.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/cave.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/desert.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/end.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/forest.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/hell.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/jungle.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/lava.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/mountain.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/plains.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/rain.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/seaside.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/snow.json");
		exportSingleFile(folder, "/settings/soundscapes/msi/swamp.json");
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
