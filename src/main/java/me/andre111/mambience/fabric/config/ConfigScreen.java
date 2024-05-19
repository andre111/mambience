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
package me.andre111.mambience.fabric.config;

import java.io.IOException;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.andre111.mambience.MAmbience;
import me.andre111.mambience.config.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ConfigScreen implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create();
			builder.setParentScreen(parent);
			builder.setTitle(Text.translatable("mambience.config.title"));
			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			builder.setSavingRunnable(() -> {
				try {
					Config.save();
				} catch(IOException e) {
					MAmbience.getLogger().error("Exception saving/applying config: "+e);
					e.printStackTrace();
				}
			});

			// General
			builder.getOrCreateCategory(Text.translatable("mambience.config.general"))
				.addEntry(entryBuilder.startTextDescription(Text.translatable("mambience.config.general.note")).build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.general.debug"), Config.debugLogging())
						.setTooltip(Text.translatable("mambience.config.general.debug.tooltip"))
						.setDefaultValue(false)
						.setSaveConsumer(Config::setDebugLogging)
						.build());
			
			// Ambient
			builder.getOrCreateCategory(Text.translatable("mambience.config.ambient"))
				.addEntry(entryBuilder.startTextDescription(Text.translatable("mambience.config.ambient.note")).build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.enable"), Config.ambientEvents().isEnabled())
						.setDefaultValue(Config.AmbientEventsConfig.DEFAULT_ENABLED)
						.setSaveConsumer(Config.ambientEvents()::setEnabled)
						.build())
				.addEntry(entryBuilder
						.startIntSlider(Text.translatable("mambience.config.volume"), (int) (Config.ambientEvents().getVolume()*100), 0, 100)
						.setDefaultValue((int) (Config.AmbientEventsConfig.DEFAULT_VOLUME*100))
						.setSaveConsumer(i -> { Config.ambientEvents().setVolume(i/100.0f); })
						.build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.ambient.stop"), Config.ambientEvents().stopSounds())
						.setTooltip(Text.translatable("mambience.config.ambient.stop.tooltip"))
						.setDefaultValue(Config.AmbientEventsConfig.DEFAULT_STOP_SOUNDS)
						.setSaveConsumer(Config.ambientEvents()::setStopSounds)
						.build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.ambient.disable_wind"), Config.ambientEvents().disableWind())
						.setTooltip(Text.translatable("mambience.config.ambient.disable_wind.tooltip"))
						.setDefaultValue(Config.AmbientEventsConfig.DEFAULT_DISABLE_WIND)
						.setSaveConsumer(Config.ambientEvents()::setDisableWind)
						.build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.ambient.trigger.attack"), Config.ambientEvents().triggerAttackSounds())
						.setDefaultValue(Config.AmbientEventsConfig.DEFAULT_TRIGGER_ATTACK_SOUNDS)
						.setSaveConsumer(Config.ambientEvents()::setTriggerAttackSounds)
						.build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.ambient.trigger.use"), Config.ambientEvents().triggerUseSounds())
						.setDefaultValue(Config.AmbientEventsConfig.DEFAULT_TRIGGER_USE_SOUNDS)
						.setSaveConsumer(Config.ambientEvents()::setTriggerUseSounds)
						.build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.ambient.trigger.held_item"), Config.ambientEvents().triggerHeldItemSounds())
						.setDefaultValue(Config.AmbientEventsConfig.DEFAULT_TRIGGER_HELD_ITEM_SOUNDS)
						.setSaveConsumer(Config.ambientEvents()::setTriggerHeldItemSounds)
						.build());
			
			// Visual Effects
			builder.getOrCreateCategory(Text.translatable("mambience.config.effects"))
				.addEntry(entryBuilder.startTextDescription(Text.translatable("mambience.config.effects.note")).build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.enable"), Config.effects().isEnabled())
						.setDefaultValue(Config.EffectsConfig.DEFAULT_ENABLED)
						.setSaveConsumer(Config.effects()::setEnabled)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.sizex"), Config.effects().getSizeX())
						.setTooltip(Text.translatable("mambience.config.sizex.tooltip"))
						.setDefaultValue(Config.EffectsConfig.DEFAULT_SIZE_X)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(Config.effects()::setSizeX)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.sizey"), Config.effects().getSizeY())
						.setTooltip(Text.translatable("mambience.config.sizey.tooltip"))
						.setDefaultValue(Config.EffectsConfig.DEFAULT_SIZE_Y)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(Config.effects()::setSizeY)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.sizez"), Config.effects().getSizeZ())
						.setTooltip(Text.translatable("mambience.config.sizez.tooltip"))
						.setDefaultValue(Config.EffectsConfig.DEFAULT_SIZE_Z)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(Config.effects()::setSizeZ)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.effects.ticks"), Config.effects().getRandomTicks())
						.setTooltip(Text.translatable("mambience.config.effects.ticks.tooltip"))
						.setDefaultValue(Config.EffectsConfig.DEFAULT_RANDOM_TICKS)
						.setMin(1)
						.setMax(1024)
						.setSaveConsumer(Config.effects()::setRandomTicks)
						.build());
			
			// Footsteps
			builder.getOrCreateCategory(Text.translatable("mambience.config.movement"))
				.addEntry(entryBuilder.startTextDescription(Text.translatable("mambience.config.movement.note")).build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.movement.footsteps.enable"), Config.movement().footstepsEnabled())
						.setDefaultValue(Config.MovementConfig.DEFAULT_FOOTSTEPS_ENABLED)
						.setSaveConsumer(Config.movement()::setFootstepsEnabled)
						.build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.movement.armor.enable"), Config.movement().armorEnabled())
						.setDefaultValue(Config.MovementConfig.DEFAULT_ARMOR_ENABLED)
						.setSaveConsumer(Config.movement()::setArmorEnabled)
						.build())
				.addEntry(entryBuilder
						.startIntSlider(Text.translatable("mambience.config.volume"), (int) (Config.movement().getVolume()*100), 0, 100)
						.setDefaultValue((int) (Config.MovementConfig.DEFAULT_VOLUME*100))
						.setSaveConsumer(i -> { Config.movement().setVolume(i/100.0f); })
						.build())
				.addEntry(entryBuilder
						.startBooleanToggle(Text.translatable("mambience.config.apply_suggested"), Config.movement().applySuggested())
						.setDefaultValue(Config.MovementConfig.DEFAULT_APPLY_SUGGESTIONS)
						.setSaveConsumer(Config.movement()::setApplySuggested)
						.build());
			
			// Scanner
			builder.getOrCreateCategory(Text.translatable("mambience.config.scanner"))
				.addEntry(entryBuilder.startTextDescription(Text.translatable("mambience.config.scanner.note")).build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.sizex"), Config.scanner().getSizeX())
						.setTooltip(Text.translatable("mambience.config.size.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_SIZE_X)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(Config.scanner()::setSizeX)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.sizey"), Config.scanner().getSizeY())
						.setTooltip(Text.translatable("mambience.config.size.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_SIZE_Y)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(Config.scanner()::setSizeY)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.sizez"), Config.scanner().getSizeZ())
						.setTooltip(Text.translatable("mambience.config.size.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_SIZE_Z)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(Config.scanner()::setSizeZ)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.scanner.interval"), Config.scanner().getInterval())
						.setTooltip(Text.translatable("mambience.config.scanner.interval.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_INTERVAL)
						.setMin(1)
						.setMax(200)
						.setSaveConsumer(Config.scanner()::setInterval)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.entitysizex"), Config.scanner().getEntitySizeX())
						.setTooltip(Text.translatable("mambience.config.size.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_ENTITY_SIZE_X)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(Config.scanner()::setEntitySizeX)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.entitysizey"), Config.scanner().getEntitySizeY())
						.setTooltip(Text.translatable("mambience.config.size.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_ENTITY_SIZE_Y)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(Config.scanner()::setEntitySizeY)
						.build())
				.addEntry(entryBuilder
						.startIntField(Text.translatable("mambience.config.entitysizez"), Config.scanner().getEntitySizeZ())
						.setTooltip(Text.translatable("mambience.config.size.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_ENTITY_SIZE_Z)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(Config.scanner()::setEntitySizeZ)
						.build());

			return builder.build();
		};
	}
}
