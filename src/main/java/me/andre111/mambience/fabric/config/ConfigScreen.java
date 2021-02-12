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
package me.andre111.mambience.fabric.config;

import java.io.IOException;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.andre111.mambience.MAmbience;
import me.andre111.mambience.config.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;

public class ConfigScreen implements ModMenuApi {
	@Override
	public String getModId() {
		return "mambience";
	}
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create();
			builder.setParentScreen(parent);
			builder.setTitle(new TranslatableText("mambience.config.title"));
			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			builder.setSavingRunnable(() -> {
				try {
					Config.save();
					Config.reloadData(MAmbience.getLogger());
				} catch(IOException e) {
					MAmbience.getLogger().error("Exception saving/applying config: "+e);
					e.printStackTrace();
				}
			});

			ConfigCategory generalCat = builder.getOrCreateCategory(new TranslatableText("mambience.config.general"));
			{
				generalCat.addEntry(entryBuilder.startTextDescription(new TranslatableText("mambience.config.general.note")).build());
				generalCat.addEntry(entryBuilder
						.startBooleanToggle(new TranslatableText("mambience.config.general.debug"), Config.debugLogging())
						.setTooltip(new TranslatableText("mambience.config.general.debug.tooltip"))
						.setDefaultValue(false)
						.setSaveConsumer(b -> { Config.setDebugLogging(b); })
						.build());
			}
			
			ConfigCategory ambientEventsCat = builder.getOrCreateCategory(new TranslatableText("mambience.config.ambient"));
			{
				ambientEventsCat.addEntry(entryBuilder.startTextDescription(new TranslatableText("mambience.config.ambient.note")).build());
				ambientEventsCat.addEntry(entryBuilder
						.startBooleanToggle(new TranslatableText("mambience.config.enable"), Config.ambientEvents().isEnabled())
						.setDefaultValue(Config.AmbientEventsConfig.DEFAULT_ENABLED)
						.setSaveConsumer(b -> { Config.ambientEvents().setEnabled(b); })
						.build());

				ambientEventsCat.addEntry(entryBuilder
						.startIntSlider(new TranslatableText("mambience.config.volume"), (int) (Config.ambientEvents().getVolume()*100), 0, 100)
						.setDefaultValue((int) (Config.AmbientEventsConfig.DEFAULT_VOLUME*100))
						.setSaveConsumer(i -> { Config.ambientEvents().setVolume(i/100.0f); })
						.build());

				ambientEventsCat.addEntry(entryBuilder
						.startBooleanToggle(new TranslatableText("mambience.config.ambient.stop"), Config.ambientEvents().isStopSounds())
						.setTooltip(new TranslatableText("mambience.config.ambient.stop.tooltip"))
						.setDefaultValue(Config.AmbientEventsConfig.DEFAULT_STOP_SOUNDS)
						.setSaveConsumer(b -> { Config.ambientEvents().setStopSounds(b); })
						.build());
			}
			
			ConfigCategory effectsCat = builder.getOrCreateCategory(new TranslatableText("mambience.config.effects"));
			{
				effectsCat.addEntry(entryBuilder.startTextDescription(new TranslatableText("mambience.config.effects.note")).build());
				effectsCat.addEntry(entryBuilder
						.startBooleanToggle(new TranslatableText("mambience.config.enable"), Config.effects().isEnabled())
						.setDefaultValue(Config.EffectsConfig.DEFAULT_ENABLED)
						.setSaveConsumer(b -> { Config.effects().setEnabled(b); })
						.build());
				
				effectsCat.addEntry(entryBuilder
						.startIntField(new TranslatableText("mambience.config.sizex"), Config.effects().getSizeX())
						.setTooltip(new TranslatableText("mambience.config.sizex.tooltip"))
						.setDefaultValue(Config.EffectsConfig.DEFAULT_SIZE_X)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(i -> { Config.effects().setSizeX(i); })
						.build());
				effectsCat.addEntry(entryBuilder
						.startIntField(new TranslatableText("mambience.config.sizey"), Config.effects().getSizeY())
						.setTooltip(new TranslatableText("mambience.config.sizey.tooltip"))
						.setDefaultValue(Config.EffectsConfig.DEFAULT_SIZE_Y)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(i -> { Config.effects().setSizeY(i); })
						.build());
				effectsCat.addEntry(entryBuilder
						.startIntField(new TranslatableText("mambience.config.sizez"), Config.effects().getSizeZ())
						.setTooltip(new TranslatableText("mambience.config.sizez.tooltip"))
						.setDefaultValue(Config.EffectsConfig.DEFAULT_SIZE_Z)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(i -> { Config.effects().setSizeZ(i); })
						.build());
				
				effectsCat.addEntry(entryBuilder
						.startIntField(new TranslatableText("mambience.config.effects.ticks"), Config.effects().getRandomTicks())
						.setTooltip(new TranslatableText("mambience.config.effects.ticks.tooltip"))
						.setDefaultValue(Config.EffectsConfig.DEFAULT_RANDOM_TICKS)
						.setMin(1)
						.setMax(1024)
						.setSaveConsumer(i -> { Config.effects().setRandomTicks(i); })
						.build());
			}
			
			ConfigCategory footstepsCat = builder.getOrCreateCategory(new TranslatableText("mambience.config.footsteps"));
			{
				footstepsCat.addEntry(entryBuilder.startTextDescription(new TranslatableText("mambience.config.footsteps.note")).build());
				footstepsCat.addEntry(entryBuilder
						.startBooleanToggle(new TranslatableText("mambience.config.enable"), Config.footsteps().isEnabled())
						.setDefaultValue(Config.FootstepConfig.DEFAULT_ENABLED)
						.setSaveConsumer(b -> { Config.footsteps().setEnabled(b); })
						.build());

				footstepsCat.addEntry(entryBuilder
						.startIntSlider(new TranslatableText("mambience.config.volume"), (int) (Config.footsteps().getVolume()*100), 0, 100)
						.setDefaultValue((int) (Config.FootstepConfig.DEFAULT_VOLUME*100))
						.setSaveConsumer(i -> { Config.footsteps().setVolume(i/100.0f); })
						.build());
			}
			
			ConfigCategory scannerCat = builder.getOrCreateCategory(new TranslatableText("mambience.config.scanner"));
			{
				scannerCat.addEntry(entryBuilder.startTextDescription(new TranslatableText("mambience.config.scanner.note")).build());
				scannerCat.addEntry(entryBuilder
						.startIntField(new TranslatableText("mambience.config.sizex"), Config.scanner().getSizeX())
						.setTooltip(new TranslatableText("mambience.config.sizex.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_SIZE_X)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(i -> { Config.scanner().setSizeX(i); })
						.build());
				scannerCat.addEntry(entryBuilder
						.startIntField(new TranslatableText("mambience.config.sizey"), Config.scanner().getSizeY())
						.setTooltip(new TranslatableText("mambience.config.sizey.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_SIZE_Y)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(i -> { Config.scanner().setSizeY(i); })
						.build());
				scannerCat.addEntry(entryBuilder
						.startIntField(new TranslatableText("mambience.config.sizez"), Config.scanner().getSizeZ())
						.setTooltip(new TranslatableText("mambience.config.sizez.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_SIZE_Z)
						.setMin(3)
						.setMax(65)
						.setSaveConsumer(i -> { Config.scanner().setSizeZ(i); })
						.build());
				
				scannerCat.addEntry(entryBuilder
						.startIntField(new TranslatableText("mambience.config.scanner.interval"), Config.scanner().getInterval())
						.setTooltip(new TranslatableText("mambience.config.scanner.interval.tooltip"))
						.setDefaultValue(Config.ScannerConfig.DEFAULT_INTERVAL)
						.setMin(1)
						.setMax(200)
						.setSaveConsumer(i -> { Config.scanner().setInterval(i); })
						.build());
			}

			return builder.build();
		};
	}
}
