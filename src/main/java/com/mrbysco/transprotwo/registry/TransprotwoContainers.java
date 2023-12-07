package com.mrbysco.transprotwo.registry;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.screen.DispatcherContainer;
import com.mrbysco.transprotwo.client.screen.FluidDispatcherContainer;
import com.mrbysco.transprotwo.client.screen.PowerDispatcherContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class TransprotwoContainers {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Transprotwo.MOD_ID);

	public static final Supplier<MenuType<DispatcherContainer>> DISPATCHER = MENU_TYPES.register("dispatcher", () ->
			IMenuTypeExtension.create((windowId, inv, data) -> new DispatcherContainer(windowId, inv, data)));

	public static final Supplier<MenuType<FluidDispatcherContainer>> FLUID_DISPATCHER = MENU_TYPES.register("fluid_dispatcher", () ->
			IMenuTypeExtension.create((windowId, inv, data) -> new FluidDispatcherContainer(windowId, inv, data)));

	public static final Supplier<MenuType<PowerDispatcherContainer>> POWER_DISPATCHER = MENU_TYPES.register("power_dispatcher", () ->
			IMenuTypeExtension.create((windowId, inv, data) -> new PowerDispatcherContainer(windowId, inv, data)));
}
