package com.mrbysco.transprotwo.block;

import com.mrbysco.transprotwo.tile.PowerDispatcherTile;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.power.PowerTransfer;
import com.mrbysco.transprotwo.util.PowerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class PowerDispatcherBlock extends AbstractDispatcherBlock {

	public PowerDispatcherBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (!worldIn.isRemote && tile instanceof PowerDispatcherTile) {
			PowerDispatcherTile dispatcherTile = (PowerDispatcherTile) tile;
			IEnergyStorage originHandler = getOriginHandler(state, worldIn, pos);
			if (!dispatcherTile.getUpgrade().getStackInSlot(0).isEmpty())
				spawnAsEntity(worldIn, pos, dispatcherTile.getUpgrade().getStackInSlot(0));
			for (AbstractTransfer abstractTransfer : dispatcherTile.getTransfers()) {
				if(abstractTransfer instanceof PowerTransfer) {
					PowerTransfer transfer = (PowerTransfer) abstractTransfer;
					originHandler.receiveEnergy(transfer.powerStack.getAmount(), false);
				}
			}
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	public IEnergyStorage getOriginHandler(BlockState state, World world, BlockPos pos) {
		Direction face = state.get(DirectionalBlock.FACING);
		if (!world.isAreaLoaded(pos.offset(face), 1) && world.getTileEntity(pos.offset(face)) == null)
			return null;
		return PowerUtil.getEnergyStorage(world.getTileEntity(pos.offset(face)), face.getOpposite());
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof PowerDispatcherTile && !worldIn.isRemote && !player.isSneaking()) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (PowerDispatcherTile) tile, pos);
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
		return new PowerDispatcherTile();
	}
}
