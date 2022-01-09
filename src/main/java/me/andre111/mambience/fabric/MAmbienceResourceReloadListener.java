package me.andre111.mambience.fabric;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.andre111.mambience.MAmbience;
import me.andre111.mambience.MAmbienceFabric;
import me.andre111.mambience.config.EffectLoader;
import me.andre111.mambience.config.EventLoader;
import me.andre111.mambience.config.FootstepLoader;
import me.andre111.mambience.config.MaterialLoader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class MAmbienceResourceReloadListener implements SimpleSynchronousResourceReloadListener {
	@Override
	public Identifier getFabricId() {
		return new Identifier("mambience", "maeffects");
	}

	@Override
	public void reload(ResourceManager manager) {
		// reset old data
		MaterialLoader.reset();
		EffectLoader.reset();
		EventLoader.reset();
		
		// load new data
		loadEntries(manager, "ma_materials", MaterialLoader::loadMaterial);
		loadEntries(manager, "ma_sounds", EventLoader::loadEvent);
		loadEntries(manager, "ma_effects", EffectLoader::loadEffect);
		loadReplaceable(manager, new Identifier("mambience:ma_footsteps.json"), FootstepLoader::reset, FootstepLoader::loadFootsteps);
		
		// notify for missing material entries
		FootstepBlockMapGenerator.scanForMissingBlockMapEntries();
	}
	
	private void loadEntries(ResourceManager manager, String startingPath, BiConsumer<String, JsonObject> callback) {
		Set<Identifier> loaded = new HashSet<>();
		for(Identifier id : manager.findResources(startingPath, path -> path.endsWith(".json"))) {
			// avoid loading duplicates (findResources does not guarantee non duplicate entries)
			if(loaded.contains(id)) continue;
			loaded.add(id);
			
			// actual loading
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(manager.getResource(id).getInputStream()))) {
				JsonElement element = JsonParser.parseReader(reader);
				if(element instanceof JsonObject object) {
					String path = id.getPath();
					Identifier actualID = new Identifier(id.getNamespace(), path.substring(startingPath.length()+1, path.length()-5));
					
					MAmbience.getLogger().log("Loading " + actualID.toString() + " - " + id.toString());
					
					callback.accept(actualID.toString(), object);
				} else {
					throw new RuntimeException("Root is not a json object");
				}
			} catch(Exception e) {
				MAmbience.getLogger().error("Exception while loading json " + id.toString() + ": " + e.getMessage());
			}
		}
	}
	
	private void loadReplaceable(ResourceManager manager, Identifier id, Runnable resetCallback, Consumer<JsonObject> callback) {
		try {
			resetCallback.run();
			for(Resource resource : manager.getAllResources(id)) {
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
					JsonElement element = JsonParser.parseReader(reader);
					if(element instanceof JsonObject object) {
						// check for replace setting and call reset of required
						if(object.has("replace") && object.get("replace").isJsonPrimitive() && object.get("replace").getAsBoolean()) {
							resetCallback.run();
						}
						
						// load data
						callback.accept(object);
					} else {
						throw new RuntimeException("Root is not a json object");
					}
				} catch(Exception e) {
					MAmbienceFabric.LOGGER.error("Exception while loading json " + id.toString(), e);
				}
			}
		} catch (Exception e) {
			MAmbienceFabric.LOGGER.error("Exception while loading json " + id.toString(), e);
		}
	}
}
