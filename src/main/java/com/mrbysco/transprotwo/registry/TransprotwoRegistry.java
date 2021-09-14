package com.mrbysco.transprotwo.registry;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.block.DispatcherBlock;
import com.mrbysco.transprotwo.block.FluidDispatcherBlock;
import com.mrbysco.transprotwo.block.PowerDispatcherBlock;
import com.mrbysco.transprotwo.item.LinkerItem;
import com.mrbysco.transprotwo.item.UpgradeItem;
import com.mrbysco.transprotwo.tile.FluidDispatcherTile;
import com.mrbysco.transprotwo.tile.ItemDispatcherTile;
import com.mrbysco.transprotwo.tile.PowerDispatcherTile;
import com.mrbysco.transprotwo.util.Boost;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TransprotwoRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Transprotwo.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Transprotwo.MOD_ID);
	public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Transprotwo.MOD_ID);

	//Blocks
	public static final RegistryObject<Block> DISPATCHER = BLOCKS.register("dispatcher", () ->  new DispatcherBlock(
			AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(1.5F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> FLUID_DISPATCHER = BLOCKS.register("fluid_dispatcher", () ->  new FluidDispatcherBlock(
			AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(1.5F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> POWER_DISPATCHER = BLOCKS.register("power_dispatcher", () ->  new PowerDispatcherBlock(
			AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(1.5F).sound(SoundType.METAL)));

	//Items
	public static final RegistryObject<Item> DISPATCHER_ITEM  = ITEMS.register("dispatcher", () -> new BlockItem(DISPATCHER.get(), new Item.Properties().group(TransprotwoTab.MAIN)));
	public static final RegistryObject<Item> FLUID_DISPATCHER_ITEM  = ITEMS.register("fluid_dispatcher", () -> new BlockItem(FLUID_DISPATCHER.get(), new Item.Properties().group(TransprotwoTab.MAIN)));
	public static final RegistryObject<Item> POWER_DISPATCHER_ITEM  = ITEMS.register("power_dispatcher", () -> new BlockItem(POWER_DISPATCHER.get(), new Item.Properties().group(TransprotwoTab.MAIN)));

	public static final RegistryObject<Item> LINKER  = ITEMS.register("linker", () -> new LinkerItem(new Item.Properties().group(ItemGroup.TRANSPORTATION)));
	public static final RegistryObject<Item> UPGRADE_MK_I  = ITEMS.register("upgrade_mk_i", () ->
			new UpgradeItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION), 0,
				new Boost((long) (Boost.defaultFrequence / 1.5), Boost.defaultSpeed * 1.5, 1)));
	public static final RegistryObject<Item> UPGRADE_MK_II  = ITEMS.register("upgrade_mk_ii", () ->
			new UpgradeItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION), 1,
				new Boost((long) (Boost.defaultFrequence / 2.5), Boost.defaultSpeed * 2.0, 4)));
	public static final RegistryObject<Item> UPGRADE_MK_III  = ITEMS.register("upgrade_mk_iii", () ->
			new UpgradeItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION), 2,
				new Boost((long) (Boost.defaultFrequence / 5.0), Boost.defaultSpeed * 4.0, 16)));
	public static final RegistryObject<Item> UPGRADE_MK_IV  = ITEMS.register("upgrade_mk_iv", () ->
			new UpgradeItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION), 3,
				new Boost((long) (Boost.defaultFrequence / 8.0), Boost.defaultSpeed * 5.0, 64)));

	//Tiles
	public static final RegistryObject<TileEntityType<ItemDispatcherTile>> DISPATCHER_TILE = TILES.register("dispatcher", () -> TileEntityType.Builder.create(() ->
			new ItemDispatcherTile(), DISPATCHER.get()).build(null));
	public static final RegistryObject<TileEntityType<FluidDispatcherTile>> FLUID_DISPATCHER_TILE = TILES.register("fluid_dispatcher", () -> TileEntityType.Builder.create(() ->
			new FluidDispatcherTile(), FLUID_DISPATCHER.get()).build(null));
	public static final RegistryObject<TileEntityType<PowerDispatcherTile>> POWER_DISPATCHER_TILE = TILES.register("power_dispatcher", () -> TileEntityType.Builder.create(() ->
			new PowerDispatcherTile(), POWER_DISPATCHER.get()).build(null));
}
