package com.mrbysco.transprotwo.registry;

import com.mrbysco.transprotwo.Transprotwo;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class TransprotwoComponents {
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Transprotwo.MOD_ID);

	public static final Supplier<DataComponentType<GlobalPos>> LINKED = DATA_COMPONENT_TYPES.register("linked", () ->
			DataComponentType.<GlobalPos>builder()
					.persistent(GlobalPos.CODEC)
					.networkSynchronized(GlobalPos.STREAM_CODEC)
					.build());
}
