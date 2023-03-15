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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.andre111.mambience.data.Data;

public class ZipFileEntryData implements Data {
	private final ZipFile file;
	private final ZipEntry entry;
	
	public ZipFileEntryData(ZipFile file, ZipEntry entry) {
		this.file = file;
		this.entry = entry;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return file.getInputStream(entry);
	}
}
