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
package me.andre111.mambience.data;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class DataLocatorFabric implements DataLocator {
	private final ResourceManager manager;
	
	public DataLocatorFabric(ResourceManager manager) {
		this.manager = manager;
	}

	@Override
	public Collection<String> findData(String startingPath, Predicate<String> pathPredicate) {
		Predicate<Identifier> idPredicate = id -> pathPredicate.test(id.getPath());
		return manager.findResources(startingPath, idPredicate).keySet().stream().map(id -> id.toString()).collect(Collectors.toList());
	}

	@Override
	public Data getData(String id) throws IOException {
		return new DataFabric(manager.getResource(new Identifier(id)).get());
	}

	@Override
	public List<Data> getAllData(String id) throws IOException {
		return manager.getAllResources(new Identifier(id)).stream().map(DataFabric::new).collect(Collectors.toList());
	}
}
