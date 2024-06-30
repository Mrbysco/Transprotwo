package com.mrbysco.transprotwo.item;

import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE;
import com.mrbysco.transprotwo.blockentity.FluidDispatcherBE;
import com.mrbysco.transprotwo.blockentity.ItemDispatcherBE;
import com.mrbysco.transprotwo.blockentity.PowerDispatcherBE;
import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.registry.TransprotwoComponents;
import com.mrbysco.transprotwo.util.DistanceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
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
				GlobalPos globalPos = GlobalPos.of(level.dimension(), pos);
				stack.set(TransprotwoComponents.LINKED, globalPos);
				player.displayClientMessage(Component.literal("Bound to Dispatcher."), true);
				return InteractionResult.SUCCESS;
			} else if (stack.has(TransprotwoComponents.LINKED)) {
				GlobalPos globalPos = stack.get(TransprotwoComponents.LINKED);
				BlockPos tPos = globalPos.pos();
				ResourceLocation location = globalPos.dimension().location();
				BlockEntity blockEntity = level.getBlockEntity(pos);
				if (blockEntity != null) {
					if (level.getCapability(Capabilities.ItemHandler.BLOCK, pos, context.getClickedFace()) != null) {
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
					}
					if (level.getCapability(Capabilities.FluidHandler.BLOCK, pos, context.getClickedFace()) != null) {
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
					}
					if (level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, context.getClickedFace()) != null) {
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
