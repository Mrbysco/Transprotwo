package com.mrbysco.transprotwo.item;

import com.mrbysco.transprotwo.config.TransprotConfig;
import com.mrbysco.transprotwo.tile.AbstractDispatcherTile;
import com.mrbysco.transprotwo.tile.FluidDispatcherTile;
import com.mrbysco.transprotwo.tile.ItemDispatcherTile;
import com.mrbysco.transprotwo.tile.PowerDispatcherTile;
import com.mrbysco.transprotwo.util.DistanceHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class LinkerItem extends Item {
	public LinkerItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		World worldIn = context.getLevel();
		if (worldIn.isClientSide)
			return ActionResultType.PASS;

		BlockPos pos = context.getClickedPos();
		PlayerEntity player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if (player.isShiftKeyDown()) {
			if (worldIn.getBlockEntity(pos) instanceof AbstractDispatcherTile) {
				CompoundNBT stackTag = stack.hasTag() ? stack.getTag() : new CompoundNBT();
				stackTag.putLong("pos", pos.asLong());
				stackTag.putString("dimension", worldIn.dimension().location().toString());
				stack.setTag(stackTag);
				player.displayClientMessage(new StringTextComponent("Bound to Dispatcher."), true);
				return ActionResultType.SUCCESS;
			} else if (stack.hasTag() && stack.getTag().contains("pos")) {
				CompoundNBT stackTag = stack.getTag();
				BlockPos tPos = BlockPos.of(stackTag.getLong("pos"));
				ResourceLocation location = ResourceLocation.tryParse(stackTag.getString("dimension"));
				TileEntity tileEntity = worldIn.getBlockEntity(pos);
				if(tileEntity != null) {
					if(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
						if (worldIn.dimension().location().equals(location) && worldIn.getBlockEntity(tPos) instanceof ItemDispatcherTile) {
							ItemDispatcherTile tile = (ItemDispatcherTile) worldIn.getBlockEntity(tPos);
							Direction facing = context.getClickedFace();
							Pair<BlockPos, Direction> pair = new ImmutablePair<>(pos, facing);
							if (DistanceHelper.getDistance(pos, tPos) < TransprotConfig.COMMON.range.get()) {
								boolean done = tile.getTargets().add(pair);
								if (done) {
									player.displayClientMessage(new StringTextComponent("Added " + worldIn.getBlockState(pos).getBlock().getRegistryName() + "."), true);
									tile.refreshClient();
								} else {
									player.displayClientMessage(new StringTextComponent("Inventory is already connected."), true);
								}
							} else
								player.displayClientMessage(new StringTextComponent("Too far away."), true);
							return ActionResultType.SUCCESS;
						}
					} else if(tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent()) {
						if (worldIn.dimension().location().equals(location) && worldIn.getBlockEntity(tPos) instanceof FluidDispatcherTile) {
							FluidDispatcherTile tile = (FluidDispatcherTile) worldIn.getBlockEntity(tPos);
							Direction facing = context.getClickedFace();
							Pair<BlockPos, Direction> pair = new ImmutablePair<>(pos, facing);
							if (DistanceHelper.getDistance(pos, tPos) < TransprotConfig.COMMON.range.get()) {
								boolean done = tile.getTargets().add(pair);
								if (done) {
									player.displayClientMessage(new StringTextComponent("Added " + worldIn.getBlockState(pos).getBlock().getRegistryName() + "."), true);
									tile.refreshClient();
								} else {
									player.displayClientMessage(new StringTextComponent("Tank is already connected."), true);
								}
							} else
								player.displayClientMessage(new StringTextComponent("Too far away."), true);
							return ActionResultType.SUCCESS;
						}
					} else if(tileEntity.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
						if (worldIn.dimension().location().equals(location) && worldIn.getBlockEntity(tPos) instanceof PowerDispatcherTile) {
							PowerDispatcherTile tile = (PowerDispatcherTile) worldIn.getBlockEntity(tPos);
							Direction facing = context.getClickedFace();
							Pair<BlockPos, Direction> pair = new ImmutablePair<>(pos, facing);
							if (DistanceHelper.getDistance(pos, tPos) < TransprotConfig.COMMON.range.get()) {
								boolean done = tile.getTargets().add(pair);
								if (done) {
									player.displayClientMessage(new StringTextComponent("Added " + worldIn.getBlockState(pos).getBlock().getRegistryName() + "."), true);
									tile.refreshClient();
								} else {
									player.displayClientMessage(new StringTextComponent("Tank is already connected."), true);
								}
							} else
								player.displayClientMessage(new StringTextComponent("Too far away."), true);
							return ActionResultType.SUCCESS;
						}
					}
				}
			}
		}
		return super.useOn(context);
	}
}
