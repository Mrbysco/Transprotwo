package com.mrbysco.transprotwo.block;

import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.blockentity.FluidDispatcherBE;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.FluidTransfer;
import com.mrbysco.transprotwo.util.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class FluidDispatcherBlock extends AbstractDispatcherBlock {

	public FluidDispatcherBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (!worldIn.isClientSide && blockEntity instanceof FluidDispatcherBE fluidDispatcher) {
			IFluidHandler originHandler = getOriginHandler(state, worldIn, pos);
			if (!fluidDispatcher.getUpgrade().getStackInSlot(0).isEmpty())
				popResource(worldIn, pos, fluidDispatcher.getUpgrade().getStackInSlot(0));
			for (AbstractTransfer abstractTransfer : fluidDispatcher.getTransfers()) {
				if (abstractTransfer instanceof FluidTransfer transfer) {
					originHandler.fill(transfer.fluidStack, FluidAction.EXECUTE);
				}
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	public IFluidHandler getOriginHandler(BlockState state, Level world, BlockPos pos) {
		Direction face = state.getValue(DirectionalBlock.FACING);
		if (!world.isAreaLoaded(pos.relative(face), 1) && world.getBlockEntity(pos.relative(face)) == null)
			return null;
		return FluidHelper.getFluidHandler(world.getBlockEntity(pos.relative(face)), face.getOpposite());
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (blockEntity instanceof FluidDispatcherBE && !worldIn.isClientSide && !player.isShiftKeyDown()) {
			NetworkHooks.openGui((ServerPlayer) player, (FluidDispatcherBE) blockEntity, pos);
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FluidDispatcherBE(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createDispatcherTicker(level, type, TransprotwoRegistry.FLUID_DISPATCHER_BLOCK_ENTITY.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createDispatcherTicker(Level level, BlockEntityType<T> p_151989_, BlockEntityType<? extends FluidDispatcherBE> p_151990_) {
		return level.isClientSide ? createTickerHelper(p_151989_, p_151990_, FluidDispatcherBE::clientTick) : createTickerHelper(p_151989_, p_151990_, FluidDispatcherBE::serverTick);
	}
}
