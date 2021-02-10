/*
 * Copyright (c) 2021 André Schweiger
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
package me.andre111.mambience.config;

import java.io.BufferedReader;
import java.io.IOException;

public final class CommentSkippingReader implements AutoCloseable {
	private BufferedReader reader;
	
	public CommentSkippingReader(BufferedReader reader) {
		this.reader = reader;
	}
	
	public String readLine() throws IOException {
		String line = null;
		while(isComment(line = reader.readLine())) {}
		return line;
	}
	
	public String readAllLines(String lineBreak) throws IOException  {
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = readLine()) != null) {
			sb.append(line);
			sb.append(lineBreak);
		}
		return sb.toString();
	}
	
	private boolean isComment(String line) {
		if(line == null) return false;
		return line.trim().startsWith("//");
	}

	@Override
	public void close() throws Exception {
		reader.close();
	}
}
