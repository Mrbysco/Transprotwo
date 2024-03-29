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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class TransprotwoRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Transprotwo.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Transprotwo.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Transprotwo.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Transprotwo.MOD_ID);

	//Blocks
	public static final RegistryObject<Block> DISPATCHER = BLOCKS.register("dispatcher", () -> new DispatcherBlock(
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL)));
	public static final net.minecraftforge.registries.RegistryObject<Block> FLUID_DISPATCHER = BLOCKS.register("fluid_dispatcher", () -> new FluidDispatcherBlock(
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> POWER_DISPATCHER = BLOCKS.register("power_dispatcher", () -> new PowerDispatcherBlock(
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL)));

	//Items
	public static final net.minecraftforge.registries.RegistryObject<Item> DISPATCHER_ITEM = ITEMS.register("dispatcher", () -> new BlockItem(DISPATCHER.get(), new Item.Properties()));
	public static final net.minecraftforge.registries.RegistryObject<Item> FLUID_DISPATCHER_ITEM = ITEMS.register("fluid_dispatcher", () -> new BlockItem(FLUID_DISPATCHER.get(), new Item.Properties()));
	public static final RegistryObject<Item> POWER_DISPATCHER_ITEM = ITEMS.register("power_dispatcher", () -> new BlockItem(POWER_DISPATCHER.get(), new Item.Properties()));

	public static final net.minecraftforge.registries.RegistryObject<Item> LINKER = ITEMS.register("linker", () -> new LinkerItem(new Item.Properties()));
	public static final net.minecraftforge.registries.RegistryObject<Item> UPGRADE_MK_I = ITEMS.register("upgrade_mk_i", () ->
			new UpgradeItem(new Item.Properties().stacksTo(1), 0,
					new Boost((long) (Boost.defaultFrequence / 1.5), Boost.defaultSpeed * 1.5, 1)));
	public static final net.minecraftforge.registries.RegistryObject<Item> UPGRADE_MK_II = ITEMS.register("upgrade_mk_ii", () ->
			new UpgradeItem(new Item.Properties().stacksTo(1), 1,
					new Boost((long) (Boost.defaultFrequence / 2.5), Boost.defaultSpeed * 2.0, 4)));
	public static final net.minecraftforge.registries.RegistryObject<Item> UPGRADE_MK_III = ITEMS.register("upgrade_mk_iii", () ->
			new UpgradeItem(new Item.Properties().stacksTo(1), 2,
					new Boost((long) (Boost.defaultFrequence / 5.0), Boost.defaultSpeed * 4.0, 16)));
	public static final RegistryObject<Item> UPGRADE_MK_IV = ITEMS.register("upgrade_mk_iv", () ->
			new UpgradeItem(new Item.Properties().stacksTo(1), 3,
					new Boost((long) (Boost.defaultFrequence / 8.0), Boost.defaultSpeed * 5.0, 64)));

	//Tiles
	public static final net.minecraftforge.registries.RegistryObject<BlockEntityType<ItemDispatcherBE>> DISPATCHER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("dispatcher", () -> BlockEntityType.Builder.of(
			ItemDispatcherBE::new, DISPATCHER.get()).build(null));
	public static final RegistryObject<BlockEntityType<FluidDispatcherBE>> FLUID_DISPATCHER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("fluid_dispatcher", () -> BlockEntityType.Builder.of(
			FluidDispatcherBE::new, FLUID_DISPATCHER.get()).build(null));
	public static final RegistryObject<BlockEntityType<PowerDispatcherBE>> POWER_DISPATCHER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("power_dispatcher", () -> BlockEntityType.Builder.of(
			PowerDispatcherBE::new, POWER_DISPATCHER.get()).build(null));


	public static final RegistryObject<CreativeModeTab> TRANSPROTWO_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(TransprotwoRegistry.DISPATCHER.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.transprotwo"))
			.displayItems((parameters, output) -> {
				List<ItemStack> stacks = TransprotwoRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
				output.acceptAll(stacks);
			}).build());
}
