package com.mrbysco.transprotwo.datagen.client;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class TransprotModelProvider extends ModelProvider {
	public TransprotModelProvider(PackOutput output) {
		super(output, Transprotwo.MOD_ID);
	}

	@Override
	protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
		// Blocks
		dispatcher(blockModels, TransprotwoRegistry.DISPATCHER.get());
		dispatcher(blockModels, TransprotwoRegistry.FLUID_DISPATCHER.get());
		dispatcher(blockModels, TransprotwoRegistry.POWER_DISPATCHER.get());

		// Items
		itemModels.generateFlatItem(TransprotwoRegistry.LINKER.get(), ModelTemplates.FLAT_ITEM);
		itemModels.generateFlatItem(TransprotwoRegistry.UPGRADE_MK_I.get(), ModelTemplates.FLAT_ITEM);
		itemModels.generateFlatItem(TransprotwoRegistry.UPGRADE_MK_II.get(), ModelTemplates.FLAT_ITEM);
		itemModels.generateFlatItem(TransprotwoRegistry.UPGRADE_MK_III.get(), ModelTemplates.FLAT_ITEM);
		itemModels.generateFlatItem(TransprotwoRegistry.UPGRADE_MK_IV.get(), ModelTemplates.FLAT_ITEM);
	}

	private void dispatcher(BlockModelGenerators blockModels, Block block) {
		ResourceLocation dispatcher = ResourceLocation.fromNamespaceAndPath(Transprotwo.MOD_ID, "block/dispatcher");
		blockModels.blockStateOutput
				.accept(
						MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, dispatcher))
								.with(PropertyDispatch.property(BlockStateProperties.FACING)
										.select(Direction.UP, Variant.variant()
												.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
										)
										.select(Direction.DOWN, Variant.variant())
										.select(Direction.EAST, Variant.variant()
												.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
												.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
										)
										.select(Direction.NORTH, Variant.variant()
												.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
												.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
										)
										.select(Direction.WEST, Variant.variant()
												.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
												.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
										)
										.select(Direction.SOUTH, Variant.variant()
												.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
												.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
										)
								)
				);
		blockModels.registerSimpleItemModel(block, dispatcher);
	}
}
