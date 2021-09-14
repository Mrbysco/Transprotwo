package com.mrbysco.transprotwo.block;

import com.mrbysco.transprotwo.tile.AbstractDispatcherTile;
import com.mrbysco.transprotwo.tile.ItemDispatcherTile;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.ItemTransfer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class DispatcherBlock extends AbstractDispatcherBlock {

	public DispatcherBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (!worldIn.isRemote && tile instanceof ItemDispatcherTile) {
			ItemDispatcherTile dispatcherTile = (ItemDispatcherTile) tile;
			if (!dispatcherTile.getUpgrade().getStackInSlot(0).isEmpty())
				spawnAsEntity(worldIn, pos, dispatcherTile.getUpgrade().getStackInSlot(0));
			for (AbstractTransfer transfer : dispatcherTile.getTransfers()) {
				if(transfer instanceof ItemTransfer) {
					ItemTransfer itemTransfer = (ItemTransfer) transfer;
					InventoryHelper.spawnItemStack(worldIn, pos.getX() + transfer.current.x, pos.getY() + transfer.current.y, pos.getZ() + transfer.current.z, itemTransfer.stack);
				}
			}
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof AbstractDispatcherTile && !worldIn.isRemote && !player.isSneaking()) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (AbstractDispatcherTile) tile, pos);
		}
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ItemDispatcherTile();
	}
}
