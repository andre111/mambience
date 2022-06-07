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
package me.andre111.mambience.fabric;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;

import me.andre111.mambience.MAmbience;
import me.andre111.mambience.MAmbienceFabric;
import me.andre111.mambience.config.Config;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistryManager;

public class ClientsideDataLoader {
	private static Map<Identifier, Map<Identifier, List<Identifier>>> TAG_MAP = new HashMap<>();
	
	public static void reloadData(DynamicRegistryManager registryManager) {
		// create clientside datapack dir
		File clientsideDatapacksDir = new File(Config.getRoot(), "clientside_datapacks"+File.separator);
		if(!clientsideDatapacksDir.exists()) {
			clientsideDatapacksDir.mkdirs();
		}
		
		// create manager to scan for datapacks (including mod builtins + cientsideDatapacksDir)
		ResourcePackManager packManager = new ResourcePackManager(ResourceType.SERVER_DATA,
				new VanillaDataPackProvider(),
				new ModResourcePackCreator(ResourceType.SERVER_DATA), 
				new FileResourcePackProvider(clientsideDatapacksDir, ResourcePackSource.nameAndSource("pack.source.mambience.clientside"))
		);
		
		// scan for and enable all packs
		packManager.scanPacks();
		packManager.setEnabledProfiles(packManager.getNames());
		
		// create actual resource manager
		LifecycledResourceManagerImpl resourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, packManager.createResourcePacks());
		// create reload listeners
		TagManagerLoader tagLoader = new TagManagerLoader(registryManager);
		
		
		// perform reload
		MAmbience.getLogger().log("Reloading clientside data...");
		//IMPORTANT NOTE: 
		// Fabric API now mixes into SimpleResourceReload.start to add custom reload listeners
		// But we don't want other mods listeners to be called
		// (as that breaks stuff because of missing data)
		// -> avoid calling start and instead directly use create (this no longer allows for profiled reloads, but that should be acceptable for now)
		ResourceReload reload = SimpleResourceReload.create(resourceManager, Lists.newArrayList(tagLoader, MAmbienceFabric.RELOAD_LISTENER), Util.getMainWorkerExecutor(), MinecraftClient.getInstance(), CompletableFuture.completedFuture(Unit.INSTANCE));
		
		// retrieve tag manager after completion
		reload.whenComplete().thenAccept(unit -> {
			// set clientside tagmanager
			MAmbience.getLogger().log("Loaded clientside tag manager");
			TAG_MAP.clear();
			tagLoader.getRegistryTags().forEach(registryTags -> registerTags(registryTags));
			
			// close all resources
			resourceManager.close();
		});
		
		// notify of exceptions
		reload.whenComplete().exceptionally(e -> {
			MAmbience.getLogger().error("Error reloading clientside data: ");
			e.printStackTrace();

			// close all resources
			resourceManager.close();
			return null;
		});
	}
	
	private static <T> void registerTags(TagManagerLoader.RegistryTags<T> registryTags) {
		// convert RegistryTags created by TagManagerLoader to simple maps using identifiers to decouple them from the actual registries
		Map<Identifier, List<Identifier>> idTags = new HashMap<>();
		for(var tag : registryTags.tags().entrySet()) {
			List<Identifier> idList = new ArrayList<>();
			
			for(var entry : tag.getValue()) {
				if(entry.getKey().isPresent()) {
					idList.add(entry.getKey().get().getValue());
				}
			}

			idTags.put(tag.getKey(), idList);
		}
		TAG_MAP.put(registryTags.key().getValue(), idTags);
	}
	
	public static List<Identifier> getTag(Identifier registry, Identifier id) {
		return TAG_MAP.getOrDefault(registry, Map.of()).getOrDefault(id, List.of());
	}
}
