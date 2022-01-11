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
import java.util.concurrent.CompletableFuture;

import me.andre111.mambience.MAmbience;
import me.andre111.mambience.MAmbienceFabric;
import me.andre111.mambience.config.Config;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.tag.TagManager;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistryManager;

public class ClientsideDataLoader {
	private static TagManager CLIENTSIDE_TAG_MANAGER = TagManager.EMPTY;
	
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
		ReloadableResourceManagerImpl resourceManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);
		// register reload listeners
		TagManagerLoader tagLoader = new TagManagerLoader(registryManager);
		resourceManager.registerReloader(tagLoader);
		resourceManager.registerReloader(MAmbienceFabric.RELOAD_LISTENER);
		
		// perform reload
		ResourceReload reload = resourceManager.reload(Util.getMainWorkerExecutor(), MinecraftClient.getInstance(), CompletableFuture.completedFuture(Unit.INSTANCE), packManager.createResourcePacks());
		reload.whenComplete().thenAccept(unit -> {
			// set clientside tagmanager
			MAmbience.getLogger().log("Loaded clientside tag manager");
			CLIENTSIDE_TAG_MANAGER = tagLoader.getTagManager();
			
			// close all resources
			resourceManager.close();
			packManager.close();
		});
		
		// notify of exceptions
		reload.whenComplete().exceptionally(e -> {
			MAmbience.getLogger().error("Error reloading clientside data: ");
			e.printStackTrace();

			// close all resources
			resourceManager.close();
			packManager.close();
			return Unit.INSTANCE;
		});
	}
	
	public static TagManager getClientsideTagManager() {
		return CLIENTSIDE_TAG_MANAGER;
	}
}
