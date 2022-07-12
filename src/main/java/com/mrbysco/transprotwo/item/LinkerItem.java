package com.mrbysco.transprotwo.item;

import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE;
import com.mrbysco.transprotwo.blockentity.FluidDispatcherBE;
import com.mrbysco.transprotwo.blockentity.ItemDispatcherBE;
import com.mrbysco.transprotwo.blockentity.PowerDispatcherBE;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.util.DistanceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class LinkerItem extends Item {
	public LinkerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level worldIn = context.getLevel();
		if (worldIn.isClientSide)
			return InteractionResult.PASS;

		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if (player.isShiftKeyDown()) {
			if (worldIn.getBlockEntity(pos) instanceof AbstractDispatcherBE) {
				CompoundTag stackTag = stack.hasTag() ? stack.getTag() : new CompoundTag();
				stackTag.putLong("pos", pos.asLong());
				stackTag.putString("dimension", worldIn.dimension().location().toString());
				stack.setTag(stackTag);
				player.displayClientMessage(Component.literal("Bound to Dispatcher."), true);
				return InteractionResult.SUCCESS;
			} else if (stack.hasTag() && stack.getTag().contains("pos")) {
				CompoundTag stackTag = stack.getTag();
				BlockPos tPos = BlockPos.of(stackTag.getLong("pos"));
				ResourceLocation location = ResourceLocation.tryParse(stackTag.getString("dimension"));
				BlockEntity blockEntity = worldIn.getBlockEntity(pos);
				if (blockEntity != null) {
					if (blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
						if (worldIn.dimension().location().equals(location) && worldIn.getBlockEntity(tPos) instanceof ItemDispatcherBE itemDispatcher) {
							Direction facing = context.getClickedFace();
							Pair<BlockPos, Direction> pair = new ImmutablePair<>(pos, facing);
							if (DistanceHelper.getDistance(pos, tPos) < TransprotConfig.COMMON.range.get()) {
								boolean done = itemDispatcher.getTargets().add(pair);
								if (done) {
									player.displayClientMessage(Component.literal("Added " + ForgeRegistries.BLOCKS.getKey(worldIn.getBlockState(pos).getBlock()) + "."), true);
									itemDispatcher.refreshClient();
								} else {
									player.displayClientMessage(Component.literal("Inventory is already connected."), true);
								}
							} else
								player.displayClientMessage(Component.literal("Too far away."), true);
							return InteractionResult.SUCCESS;
						}
					} else if (blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent()) {
						if (worldIn.dimension().location().equals(location) && worldIn.getBlockEntity(tPos) instanceof FluidDispatcherBE fluidDispatcher) {
							Direction facing = context.getClickedFace();
							Pair<BlockPos, Direction> pair = new ImmutablePair<>(pos, facing);
							if (DistanceHelper.getDistance(pos, tPos) < TransprotConfig.COMMON.range.get()) {
								boolean done = fluidDispatcher.getTargets().add(pair);
								if (done) {
									player.displayClientMessage(Component.literal("Added " + ForgeRegistries.BLOCKS.getKey(worldIn.getBlockState(pos).getBlock()) + "."), true);
									fluidDispatcher.refreshClient();
								} else {
									player.displayClientMessage(Component.literal("Tank is already connected."), true);
								}
							} else
								player.displayClientMessage(Component.literal("Too far away."), true);
							return InteractionResult.SUCCESS;
						}
					} else if (blockEntity.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
						if (worldIn.dimension().location().equals(location) && worldIn.getBlockEntity(tPos) instanceof PowerDispatcherBE powerDispatcher) {
							Direction facing = context.getClickedFace();
							Pair<BlockPos, Direction> pair = new ImmutablePair<>(pos, facing);
							if (DistanceHelper.getDistance(pos, tPos) < TransprotConfig.COMMON.range.get()) {
								boolean done = powerDispatcher.getTargets().add(pair);
								if (done) {
									player.displayClientMessage(Component.literal("Added " + ForgeRegistries.BLOCKS.getKey(worldIn.getBlockState(pos).getBlock()) + "."), true);
									powerDispatcher.refreshClient();
								} else {
									player.displayClientMessage(Component.literal("Tank is already connected."), true);
								}
							} else
								player.displayClientMessage(Component.literal("Too far away."), true);
							return InteractionResult.SUCCESS;
						}
					}
				}
			}
		}
		return super.useOn(context);
	}
}
