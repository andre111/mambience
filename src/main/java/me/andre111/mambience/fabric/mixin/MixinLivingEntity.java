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
package me.andre111.mambience.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.andre111.mambience.MATrigger;
import me.andre111.mambience.MAmbience;
import me.andre111.mambience.MAmbienceFabric;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
	@Shadow
	public boolean handSwinging;
	
	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@SuppressWarnings("resource")
	@Inject(at = @At(value = "HEAD"), method = "swingHand(Lnet/minecraft/util/Hand;Z)V", cancellable = true)
	private void swingHand(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
		if(!this.handSwinging && hand == Hand.MAIN_HAND && (Object) this instanceof PlayerEntity) {
			if(this.getWorld().isClient && !MAmbienceFabric.instance.runClientSide) return;
			
			MAmbience.getScheduler().triggerEvents(this.getUuid(), MATrigger.ATTACK_SWING);
		}
	}
}
