package me.andre111.mambience.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import me.andre111.mambience.MAmbience;
import me.andre111.mambience.script.MAScripting;
import me.andre111.mambience.script.Variables;

public class EngineConfig {
	public static int SIZEX = 32;
	public static int SIZEY = 16;
	public static int SIZEZ = 32;
	public static boolean STOPSOUNDS = false;
	
	public static void initialize(MAmbience plugin) {
		exportSettings(plugin);
		JsonParser parser = new JsonParser();
		
		File engine = new File(plugin.getDataFolder(), "/settings/engine.json");
		try {
			JsonObject engineElement = parser.parse(new FileReader(engine)).getAsJsonObject();
			
			loadSettings(engineElement.get("Settings").getAsJsonObject());
			loadVariables(engineElement.get("Variables").getAsJsonArray());
			loadMacros(engineElement.get("Macros").getAsJsonArray());
			loadSoundscapes(plugin, engineElement.get("Soundscapes").getAsJsonArray());
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			plugin.error("Exception reading engine config: "+e);
		}
	}
	
	private static void exportSettings(MAmbience plugin) {
		File folder = plugin.getDataFolder();
		if(!folder.exists()) {
			folder.mkdir();
		}
		
		exportSingleFile(plugin, folder, "settings/engine.json");
		
		exportSingleFile(plugin, folder, "settings/soundscapes/ambiotic/background.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/ambiotic/beach.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/ambiotic/coldforest.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/ambiotic/forest.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/ambiotic/jungle.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/ambiotic/plains.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/ambiotic/savanna.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/ambiotic/swamp.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/ambiotic/water.json");
		
		//TODO: Replace most (longer) sounds from RP with sounds from MSI (MAtmos) as well as extending
		//C:\Users\André\Downloads\Minecraft Sound Improvement 1.40\matmos\packs\matmos_default\assets\minecraft\sound\matmos_MSI
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/xshared.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/cave.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/desert.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/end.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/forest.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/hell.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/jungle.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/lava.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/mountain.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/plains.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/rain.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/seaside.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/snow.json");
		exportSingleFile(plugin, folder, "settings/soundscapes/msi/swamp.json");
	}
	private static void exportSingleFile(MAmbience plugin, File folder, String path) {
		if(!new File(folder, path).exists()) plugin.saveResource(path, false);
	}
	
	private static void loadSettings(JsonObject element) {
		SIZEX = element.get("ScannnerX").getAsInt();
		SIZEY = element.get("ScannnerY").getAsInt();
		SIZEZ = element.get("ScannnerZ").getAsInt();
		STOPSOUNDS = element.get("StopSounds").getAsBoolean();
	}
	
	private static void loadSoundscapes(MAmbience plugin, JsonArray array) {
		for(int i=0; i<array.size(); i++) {
			String name = array.get(i).getAsString();
			SoundscapeConfig.loadSoundscape(plugin, new File(plugin.getDataFolder(), "/settings/"+name));
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
				Material[] materials = new Material[matArray.size()];
				for(int j=0; j<matArray.size(); j++) {
					materials[j] = Material.valueOf(matArray.get(j).getAsString());
				}
				Variables.addBlockCountVariable(name, materials);
				break;
			}
			case "BiomeCount": {
				JsonArray biomeArray = variable.get("Biomes").getAsJsonArray();
				Biome[] biomes = new Biome[biomeArray.size()];
				for(int j=0; j<biomeArray.size(); j++) {
					biomes[j] = Biome.valueOf(biomeArray.get(j).getAsString());
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
