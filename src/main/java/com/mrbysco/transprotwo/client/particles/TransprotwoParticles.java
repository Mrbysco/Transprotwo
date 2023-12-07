package com.mrbysco.transprotwo.client.particles;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.particles.factory.SquareParticleType;
import com.mrbysco.transprotwo.client.particles.factory.SquareParticleTypeData;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Transprotwo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TransprotwoParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Transprotwo.MOD_ID);

	public static final Supplier<ParticleType<SquareParticleTypeData>> SQUARE_TYPE = PARTICLE_TYPES.register("square", SquareParticleType::new);

	@SubscribeEvent
	public static void registerFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(SQUARE_TYPE.get(), SquareParticleData::new);
	}
}
