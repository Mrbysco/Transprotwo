package com.mrbysco.transprotwo.block;

import com.mrbysco.transprotwo.tile.FluidDispatcherTile;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.FluidTransfer;
import com.mrbysco.transprotwo.util.FluidHelper;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class FluidDispatcherBlock extends AbstractDispatcherBlock {

	public FluidDispatcherBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getBlockEntity(pos);
		if (!worldIn.isClientSide && tile instanceof FluidDispatcherTile) {
			FluidDispatcherTile dispatcherTile = (FluidDispatcherTile) tile;
			IFluidHandler originHandler = getOriginHandler(state, worldIn, pos);
			if (!dispatcherTile.getUpgrade().getStackInSlot(0).isEmpty())
				popResource(worldIn, pos, dispatcherTile.getUpgrade().getStackInSlot(0));
			for (AbstractTransfer abstractTransfer : dispatcherTile.getTransfers()) {
				if(abstractTransfer instanceof FluidTransfer) {
					FluidTransfer transfer = (FluidTransfer) abstractTransfer;
					originHandler.fill(transfer.fluidStack, FluidAction.EXECUTE);
				}
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	public IFluidHandler getOriginHandler(BlockState state, World world, BlockPos pos) {
		Direction face = state.getValue(DirectionalBlock.FACING);
		if (!world.isAreaLoaded(pos.relative(face), 1) && world.getBlockEntity(pos.relative(face)) == null)
			return null;
		return FluidHelper.getFluidHandler(world.getBlockEntity(pos.relative(face)), face.getOpposite());
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getBlockEntity(pos);
		if(tile instanceof FluidDispatcherTile && !worldIn.isClientSide && !player.isShiftKeyDown()) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (FluidDispatcherTile) tile, pos);
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new FluidDispatcherTile();
	}
}
