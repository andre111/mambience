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

/**
 * Defines an interface for locating and accessing data based definitions.
 * This interface closely resembles the vanilla datapack mechanisms.
 * Implementations SHOULD simply map this to the corresponding vanilla code whenever possible.
 * 
 * @author André Schweiger
 */
public interface DataLocator {
	public Collection<String> findData(String startingPath, Predicate<String> pathPredicate);
	
	public Data getData(String id) throws IOException;
	public List<Data> getAllData(String id) throws IOException;
}
