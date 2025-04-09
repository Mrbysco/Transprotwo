package com.mrbysco.transprotwo.registry;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.block.DispatcherBlock;
import com.mrbysco.transprotwo.block.FluidDispatcherBlock;
import com.mrbysco.transprotwo.block.PowerDispatcherBlock;
import com.mrbysco.transprotwo.blockentity.FluidDispatcherBE;
import com.mrbysco.transprotwo.blockentity.ItemDispatcherBE;
import com.mrbysco.transprotwo.blockentity.PowerDispatcherBE;
import com.mrbysco.transprotwo.item.LinkerItem;
import com.mrbysco.transprotwo.item.UpgradeItem;
import com.mrbysco.transprotwo.util.Boost;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class TransprotwoRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Transprotwo.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Transprotwo.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Transprotwo.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Transprotwo.MOD_ID);

	//Blocks
	public static final DeferredBlock<DispatcherBlock> DISPATCHER = BLOCKS.registerBlock("dispatcher", (properties) -> new DispatcherBlock(
			properties.mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL)));
	public static final DeferredBlock<FluidDispatcherBlock> FLUID_DISPATCHER = BLOCKS.registerBlock("fluid_dispatcher", (properties) -> new FluidDispatcherBlock(
			properties.mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL)));
	public static final DeferredBlock<PowerDispatcherBlock> POWER_DISPATCHER = BLOCKS.registerBlock("power_dispatcher", (properties) -> new PowerDispatcherBlock(
			properties.mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL)));

	//Items
	public static final DeferredItem<BlockItem> DISPATCHER_ITEM = ITEMS.registerSimpleBlockItem(DISPATCHER);
	public static final DeferredItem<BlockItem> FLUID_DISPATCHER_ITEM = ITEMS.registerSimpleBlockItem(FLUID_DISPATCHER);
	public static final DeferredItem<BlockItem> POWER_DISPATCHER_ITEM = ITEMS.registerSimpleBlockItem(POWER_DISPATCHER);

	public static final DeferredItem<LinkerItem> LINKER = ITEMS.registerItem("linker", (properties) -> new LinkerItem(properties));
	public static final DeferredItem<UpgradeItem> UPGRADE_MK_I = ITEMS.registerItem("upgrade_mk_i", (properties) ->
			new UpgradeItem(properties.stacksTo(1), 0,
					new Boost((long) (Boost.defaultFrequence / 1.5), Boost.defaultSpeed * 1.5, 1)));
	public static final DeferredItem<UpgradeItem> UPGRADE_MK_II = ITEMS.registerItem("upgrade_mk_ii", (properties) ->
			new UpgradeItem(properties.stacksTo(1), 1,
					new Boost((long) (Boost.defaultFrequence / 2.5), Boost.defaultSpeed * 2.0, 4)));
	public static final DeferredItem<UpgradeItem> UPGRADE_MK_III = ITEMS.registerItem("upgrade_mk_iii", (properties) ->
			new UpgradeItem(properties.stacksTo(1), 2,
					new Boost((long) (Boost.defaultFrequence / 5.0), Boost.defaultSpeed * 4.0, 16)));
	public static final DeferredItem<UpgradeItem> UPGRADE_MK_IV = ITEMS.registerItem("upgrade_mk_iv", (properties) ->
			new UpgradeItem(properties.stacksTo(1), 3,
					new Boost((long) (Boost.defaultFrequence / 8.0), Boost.defaultSpeed * 5.0, 64)));

	//Tiles
	public static final Supplier<BlockEntityType<ItemDispatcherBE>> DISPATCHER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("dispatcher", () -> new BlockEntityType<>(
			ItemDispatcherBE::new, DISPATCHER.get()));
	public static final Supplier<BlockEntityType<FluidDispatcherBE>> FLUID_DISPATCHER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("fluid_dispatcher", () -> new BlockEntityType<>(
			FluidDispatcherBE::new, FLUID_DISPATCHER.get()));
	public static final Supplier<BlockEntityType<PowerDispatcherBE>> POWER_DISPATCHER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("power_dispatcher", () -> new BlockEntityType<>(
			PowerDispatcherBE::new, POWER_DISPATCHER.get()));

	public static final Supplier<CreativeModeTab> TRANSPROTWO_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(TransprotwoRegistry.DISPATCHER.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.transprotwo"))
			.displayItems((parameters, output) -> {
				List<ItemStack> stacks = TransprotwoRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
				output.acceptAll(stacks);
			}).build());
}
