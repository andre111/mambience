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
package me.andre111.mambience.condition;

import java.util.ArrayList;
import java.util.List;

import me.andre111.mambience.MAPlayer;

public final class ConditionHeldItem extends Condition {
	private final String itemOrTag;
	private final boolean mainHand;
	
	private List<String> cachedItems;
	
	public ConditionHeldItem(String itemOrTag, boolean mainHand) {
		if(itemOrTag == null) throw new IllegalArgumentException("Item / Itemtag cannot be null");
		
		this.itemOrTag = itemOrTag;
		this.mainHand = mainHand;
	}

	@Override
	public boolean matches(MAPlayer player) {
		// cache actual item names
		if(cachedItems == null) {
			cachedItems = new ArrayList<>();
			if(itemOrTag.startsWith("#")) {
				cachedItems.addAll(player.getAccessor().getItemTag(itemOrTag.substring(1)));
			} else {
				cachedItems.add(itemOrTag);
			}
		}
		
		// perform check
		return cachedItems.contains(player.getAccessor().getHeldItem(mainHand));
	}
}
