package com.mrbysco.transprotwo.client.particles;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.particles.factory.SquareParticleType;
import com.mrbysco.transprotwo.client.particles.factory.SquareParticleTypeData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Transprotwo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TransprotwoParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Transprotwo.MOD_ID);

	public static final RegistryObject<ParticleType<SquareParticleTypeData>> SQUARE_TYPE = PARTICLE_TYPES.register("square", SquareParticleType::new);

	@SubscribeEvent
	public static void registerFactories(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(SQUARE_TYPE.get(), SquareParticleData::new);
	}
}
