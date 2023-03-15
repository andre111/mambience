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
package me.andre111.mambience;

public class MATrigger {
	// implemented in universal code
	public static final String SECOND = "SECOND";
	public static final String TICK = "TICK";
	
	// implemented in platform specific code
	public static final String ATTACK_SWING = "ATTACK_SWING";
	public static final String ATTACK_BLOCK = "ATTACK_BLOCK";
	public static final String ATTACK_HIT = "ATTACK_HIT";
	
	public static final String USE_ITEM_MAINHAND = "USE_ITEM_MAINHAND";
	public static final String USE_ITEM_OFFHAND = "USE_ITEM_OFFHAND";
}
