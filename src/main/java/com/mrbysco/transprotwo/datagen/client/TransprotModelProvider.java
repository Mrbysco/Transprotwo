package com.mrbysco.transprotwo.datagen.client;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
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
		ResourceLocation dispatcher = Transprotwo.modLoc("block/dispatcher");
		MultiVariant variant = BlockModelGenerators.plainVariant(dispatcher);
		blockModels.blockStateOutput
				.accept(
						MultiVariantGenerator.dispatch(block)
								.with(
										PropertyDispatch.initial(BlockStateProperties.FACING)
												.select(Direction.UP, variant
														.with(BlockModelGenerators.X_ROT_180)
												)
												.select(Direction.DOWN, variant)
												.select(Direction.EAST, variant
														.with(BlockModelGenerators.Y_ROT_90)
														.with(BlockModelGenerators.X_ROT_270)
												)
												.select(Direction.NORTH, variant
														.with(BlockModelGenerators.Y_ROT_180)
														.with(BlockModelGenerators.X_ROT_90)
												)
												.select(Direction.WEST, variant
														.with(BlockModelGenerators.Y_ROT_270)
														.with(BlockModelGenerators.X_ROT_270)
												)
												.select(Direction.SOUTH, variant
														.with(BlockModelGenerators.Y_ROT_180)
														.with(BlockModelGenerators.X_ROT_270)
												)
								)
				);
		blockModels.registerSimpleItemModel(block, dispatcher);
	}
}
