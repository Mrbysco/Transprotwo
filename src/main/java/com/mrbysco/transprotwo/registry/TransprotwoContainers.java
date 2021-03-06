package com.mrbysco.transprotwo.registry;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.screen.DispatcherContainer;
import com.mrbysco.transprotwo.client.screen.FluidDispatcherContainer;
import com.mrbysco.transprotwo.client.screen.PowerDispatcherContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TransprotwoContainers {
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Transprotwo.MOD_ID);

	public static final RegistryObject<ContainerType<DispatcherContainer>> DISPATCHER = CONTAINERS.register("dispatcher", () ->
			IForgeContainerType.create((windowId, inv, data) -> new DispatcherContainer(windowId, inv, data)));

	public static final RegistryObject<ContainerType<FluidDispatcherContainer>> FLUID_DISPATCHER = CONTAINERS.register("fluid_dispatcher", () ->
			IForgeContainerType.create((windowId, inv, data) -> new FluidDispatcherContainer(windowId, inv, data)));

	public static final RegistryObject<ContainerType<PowerDispatcherContainer>> POWER_DISPATCHER = CONTAINERS.register("power_dispatcher", () ->
			IForgeContainerType.create((windowId, inv, data) -> new PowerDispatcherContainer(windowId, inv, data)));
}
