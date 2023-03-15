/*
 * Copyright (c) 2023 Andre Schweiger
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
package me.andre111.mambience.data.fallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.andre111.mambience.MAmbience;
import me.andre111.mambience.data.Data;
import me.andre111.mambience.data.DataLocator;

/**
 * Provides a very basic fallback implementation roughly imitating datapack funtionality.
 * THIS IS ONLY MEANT AS A LAST FALLBACK WHEN NO ACCESS TO VANILLA DATAPACK CODE IS POSSIBLE.
 * Current limitations include it only working with zip packed datapacks.
 * "Datapacks" will always be ordered alphabetically and EARLIER ones will take precedence.
 * Advanced datapack features such as the filters introduced in vanilla 1.19 are not supported.
 * 
 * @author André Schweiger
 */
public class FallbackDatapackDataLocator implements DataLocator {
	private final List<ZipFile> datapackZipFiles = new ArrayList<>();
	
	public FallbackDatapackDataLocator(File datapackDirectory) {
		// collect and sort all files
		File[] files = datapackDirectory.listFiles();
		Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
		
		// iterate and open all zip files
		for(File file : datapackDirectory.listFiles()) {
			if(file.getName().toLowerCase().endsWith(".zip")) {
				try {
					datapackZipFiles.add(new ZipFile(file));
				} catch (IOException e) {
					MAmbience.getLogger().error("Skipping datapack: "+file.getName()+" - not a valid zip file?");
				}
			}
		}
	}

	@Override
	public Collection<String> findData(String startingPath, Predicate<String> pathPredicate) {
		Set<String> ids = new HashSet<>();
		for(ZipFile zipFile : datapackZipFiles) {
			zipFile.stream().map(entry -> entry.getName()).filter(path -> {
				path = path.replaceFirst("data/", ""); // skip data/
				path = path.substring(path.indexOf('/')+1); // skip namespace
				return path.startsWith(startingPath+"/") && pathPredicate.test(path);
			}).forEach(path -> ids.add(pathToID(path)));
		}
		return ids;
	}

	@Override
	public Data getData(String id) throws IOException {
		String path = idToPath(id);
		for(ZipFile zipFile : datapackZipFiles) {
			ZipEntry entry = zipFile.getEntry(path);
			if(entry != null) {
				return new ZipFileEntryData(zipFile, entry);
			}
		}
		return null;
	}

	@Override
	public List<Data> getAllData(String id) throws IOException {
		String path = idToPath(id);
		List<Data> data = new ArrayList<>();
		for(ZipFile zipFile : datapackZipFiles) {
			ZipEntry entry = zipFile.getEntry(path);
			if(entry != null) {
				data.add(0, new ZipFileEntryData(zipFile, entry));
			}
		}
		return data;
	}

	public void close() {
		for(ZipFile zipFile : datapackZipFiles) {
			try {
				zipFile.close();
			} catch (IOException e) {
			}
		}
	}
	
	private String idToPath(String id) {
		String namespace = id.substring(0, id.indexOf(':'));
		String path = id.substring(id.indexOf(':')+1);
		
		return "data/"+namespace+"/"+path;
	}
	
	private String pathToID(String path) {
		return path.replaceFirst("data/", "").replaceFirst("/", ":");
	}
}
