package me.andre111.mambience.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

	@Inject(at = @At(value = "HEAD"), method = "swingHand(Lnet/minecraft/util/Hand;Z)V", cancellable = true)
	public void swingHand(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
		if(!this.handSwinging && hand == Hand.MAIN_HAND && (Object) this instanceof PlayerEntity) {
			if(this.world.isClient && !MAmbienceFabric.instance.runClientSide) return;
			
			MAmbience.getScheduler().triggerEvents(this.getUuid(), "ATTACK_SWING");
		}
	}
}
