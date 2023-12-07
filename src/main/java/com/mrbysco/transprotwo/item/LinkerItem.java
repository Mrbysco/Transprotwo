package com.mrbysco.transprotwo.item;

import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE;
import com.mrbysco.transprotwo.blockentity.FluidDispatcherBE;
import com.mrbysco.transprotwo.blockentity.ItemDispatcherBE;
import com.mrbysco.transprotwo.blockentity.PowerDispatcherBE;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.util.DistanceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.neoforge.common.capabilities.Capabilities;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class LinkerItem extends Item {
	public LinkerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (level.isClientSide)
			return InteractionResult.PASS;

		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if (player.isShiftKeyDown()) {
			if (level.getBlockEntity(pos) instanceof AbstractDispatcherBE) {
				CompoundTag stackTag = stack.hasTag() ? stack.getTag() : new CompoundTag();
				stackTag.putLong("pos", pos.asLong());
				stackTag.putString("dimension", level.dimension().location().toString());
				stack.setTag(stackTag);
				player.displayClientMessage(Component.literal("Bound to Dispatcher."), true);
				return InteractionResult.SUCCESS;
			} else if (stack.hasTag() && stack.getTag().contains("pos")) {
				CompoundTag stackTag = stack.getTag();
				BlockPos tPos = BlockPos.of(stackTag.getLong("pos"));
				ResourceLocation location = ResourceLocation.tryParse(stackTag.getString("dimension"));
				BlockEntity blockEntity = level.getBlockEntity(pos);
				if (blockEntity != null) {
					if (blockEntity.getCapability(Capabilities.ITEM_HANDLER).isPresent()) {
						if (level.dimension().location().equals(location) && level.getBlockEntity(tPos) instanceof ItemDispatcherBE itemDispatcher) {
							Direction facing = context.getClickedFace();
							Pair<BlockPos, Direction> pair = new ImmutablePair<>(pos, facing);
							if (DistanceHelper.getDistance(pos, tPos) < TransprotConfig.COMMON.range.get()) {
								boolean done = itemDispatcher.getTargets().add(pair);
								if (done) {
									player.displayClientMessage(Component.literal("Added " + BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()) + "."), true);
									itemDispatcher.refreshClient();
								} else {
									player.displayClientMessage(Component.literal("Inventory is already connected."), true);
								}
							} else
								player.displayClientMessage(Component.literal("Too far away."), true);
							return InteractionResult.SUCCESS;
						}
					} else if (blockEntity.getCapability(Capabilities.FLUID_HANDLER).isPresent()) {
						if (level.dimension().location().equals(location) && level.getBlockEntity(tPos) instanceof FluidDispatcherBE fluidDispatcher) {
							Direction facing = context.getClickedFace();
							Pair<BlockPos, Direction> pair = new ImmutablePair<>(pos, facing);
							if (DistanceHelper.getDistance(pos, tPos) < TransprotConfig.COMMON.range.get()) {
								boolean done = fluidDispatcher.getTargets().add(pair);
								if (done) {
									player.displayClientMessage(Component.literal("Added " + BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()) + "."), true);
									fluidDispatcher.refreshClient();
								} else {
									player.displayClientMessage(Component.literal("Tank is already connected."), true);
								}
							} else
								player.displayClientMessage(Component.literal("Too far away."), true);
							return InteractionResult.SUCCESS;
						}
					} else if (blockEntity.getCapability(Capabilities.ENERGY).isPresent()) {
						if (level.dimension().location().equals(location) && level.getBlockEntity(tPos) instanceof PowerDispatcherBE powerDispatcher) {
							Direction facing = context.getClickedFace();
							Pair<BlockPos, Direction> pair = new ImmutablePair<>(pos, facing);
							if (DistanceHelper.getDistance(pos, tPos) < TransprotConfig.COMMON.range.get()) {
								boolean done = powerDispatcher.getTargets().add(pair);
								if (done) {
									player.displayClientMessage(Component.literal("Added " + BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()) + "."), true);
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
