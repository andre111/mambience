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
package me.andre111.mambience.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class Generator {
	public static void generate(String dir) {
		generate(dir, "./");
	}
	
	public static void generate(String dir, String targetPath) {
		try {
			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			Path path = getFilePath(dir, targetPath);
			Files.deleteIfExists(path);
			URI uri = URI.create("jar:" + path.toUri());

			try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
				// create pack.mcmeta
				transferFile("/pack_template.mcmeta", fs.getPath("/pack.mcmeta"));
				transferFile("/pack.png", fs.getPath("/pack.png"));

				// iterate all asset directories and transfer files
				Set<String> filePaths = getResourceFiles(dir+"/");
				for(String filePath : filePaths) {
					transferFile("/"+filePath, fs.getPath("/"+filePath));
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static Path getFilePath(String dir, String targetPath) {
		return Paths.get(targetPath + "Mambience-"+getVersion()+"-"+dir+".zip");
	}
	
	private static String getVersion() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Generator.class.getResourceAsStream("/version.txt"), "UTF-8"))) {
			return reader.readLine();
		} catch(IOException e) {
			return "unkown";
		}
	}

	private static Set<String> getResourceFiles(String startPath) throws IOException {
		// find location of own class file
		String me = Generator.class.getName().replace(".", "/") + ".class";
		URL dirURL = Generator.class.getClassLoader().getResource(me);

		if (dirURL.getProtocol().equals("jar")) {
			// strip out only the JAR file
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
			try (JarFile jar = new JarFile(jarPath)) {

				// gives ALL entries in jar
				Enumeration<JarEntry> entries = jar.entries();
				Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (name.startsWith(startPath)) { // filter according to the path
						result.add(name);
					}
				}
				return result;
			}
		} else {
			throw new UnsupportedOperationException("Can only extract resources from jar file!");
		}
	}

	private static void transferFile(String sourcePath, Path targetPath) throws IOException {
		if(Files.isDirectory(targetPath)) return;
		
		try (InputStream is = Generator.class.getResourceAsStream(sourcePath)) {
			if(!Files.exists(targetPath.getParent())) {
				Files.createDirectories(targetPath.getParent());
			}
			Files.copy(is, targetPath);
		}
	}
}
