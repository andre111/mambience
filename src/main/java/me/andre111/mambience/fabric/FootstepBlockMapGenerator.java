/*
 * Copyright (c) 2024 Andre Schweiger
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

import java.util.HashMap;
import java.util.Map;

import me.andre111.mambience.MAmbience;
import me.andre111.mambience.data.loader.FootstepLoader;
import me.andre111.mambience.config.Config;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;

public class FootstepBlockMapGenerator {
	private static final Map<BlockSoundGroup, String> DEFAULT_SOUND_MAP = new HashMap<>();
	static {
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.WOOD, "planks");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.GRAVEL, "gravel");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.GRASS, "grass");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.LILY_PAD, "grass");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.STONE, "stone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.METAL, "hardmetal");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.GLASS, "glass");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.WOOL, "rug");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SAND, "sand");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SNOW, "snow");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.POWDER_SNOW, "snow");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.LADDER, "ladder");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.ANVIL, "metalcompressed,hardmetal");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SLIME, "mud");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.HONEY, "mud");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.WET_GRASS, "organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.CORAL, "");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.BAMBOO, "log");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.BAMBOO_SAPLING, "grass");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SCAFFOLDING, "squeakywood");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SWEET_BERRY_BUSH, "grass");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.CROP, "grass");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.STEM, "grass");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.VINE, "straw");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHER_WART, "organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.LANTERN, "metalbar");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHER_STEM, "grass");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NYLIUM, "grass,organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.FUNGUS, "organic_dry");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.ROOTS, "straw");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SHROOMLIGHT, "shroomlight");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.WEEPING_VINES, "grass,organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.WEEPING_VINES_LOW_PITCH, "grass,organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SOUL_SAND, "soulsand");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SOUL_SOIL, "soulsoil");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.BASALT, "basalt");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.WART_BLOCK, "organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHERRACK, "netherrack");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHER_BRICKS, "netherbrick");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHER_SPROUTS, "grass");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHER_ORE, "netherrack");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.BONE, "bone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHERITE, "hardmetal");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.ANCIENT_DEBRIS, "hardmetal");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.LODESTONE, "stonemachine");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.CHAIN, "metalbar");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHER_GOLD_ORE, "netherrack");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.GILDED_BLACKSTONE, "stone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.CANDLE, "");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.AMETHYST_BLOCK, "amethyst");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.AMETHYST_CLUSTER, "amethyst");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SMALL_AMETHYST_BUD, "amethyst");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.MEDIUM_AMETHYST_BUD, "amethyst");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.LARGE_AMETHYST_BUD, "amethyst");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.TUFF, "stone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.CALCITE, "stone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.DRIPSTONE_BLOCK, "stone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.POINTED_DRIPSTONE, "stone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.COPPER, "hardmetal");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.CAVE_VINES, "grass,organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SPORE_BLOSSOM, "brush_straw_transition,organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.AZALEA, "leaves");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.FLOWERING_AZALEA, "leaves");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.MOSS_CARPET, "moss");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.MOSS_BLOCK, "moss");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.BIG_DRIPLEAF, "grass,organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SMALL_DRIPLEAF, "grass,organic");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.ROOTED_DIRT, "dirt,log");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.HANGING_ROOTS, "");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.AZALEA_LEAVES, "leaves");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SCULK_SENSOR, "sculk");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SCULK_CATALYST, "sculk");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SCULK, "sculk");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SCULK_VEIN, "sculk");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.SCULK_SHRIEKER, "sculk");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.GLOW_LICHEN, "");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.DEEPSLATE, "deepslate");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.DEEPSLATE_BRICKS, "deepslate,brickstone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.DEEPSLATE_TILES, "deepslate,brickstone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.POLISHED_DEEPSLATE, "deepslate");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.FROGLIGHT, "organic,glowstone");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.FROGSPAWN, "");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.MANGROVE_ROOTS, "straw,log");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.MUDDY_MANGROVE_ROOTS, "straw,log,mud");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.MUD, "mud");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.MUD_BRICKS, "mudbrick");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.PACKED_MUD, "dirt");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.HANGING_SIGN, "log");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHER_WOOD_HANGING_SIGN, "log");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.BAMBOO_WOOD_HANGING_SIGN, "bamboo");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.BAMBOO_WOOD, "bamboo");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.NETHER_WOOD, "log");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.CHISELED_BOOKSHELF, "planks");
		DEFAULT_SOUND_MAP.put(BlockSoundGroup.CHERRY_WOOD, "planks");
	}
	
	public static void scanForMissingBlockMapEntries() {
		Registries.BLOCK.forEach(block -> {
			String id = Registries.BLOCK.getId(block).toString();
			if(!FootstepLoader.BLOCK_MAP.containsKey(id)) {
				String type = DEFAULT_SOUND_MAP.get(block.getDefaultState().getSoundGroup());
				if (Config.movement().applySuggested() && type != null) {
					FootstepLoader.addBlock(id, type);
				} else {
					MAmbience.getLogger().error("\""+id+"\" is missing a footstep type entry - suggested: \""+type+"\"");
				}
			}
		});
	}
}
