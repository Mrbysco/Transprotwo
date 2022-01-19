package com.mrbysco.transprotwo.block;

import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE;
import com.mrbysco.transprotwo.blockentity.ItemDispatcherBE;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.ItemTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class DispatcherBlock extends AbstractDispatcherBlock {

	public DispatcherBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!level.isClientSide && blockEntity instanceof ItemDispatcherBE itemDispatcher) {
			if (!itemDispatcher.getUpgrade().getStackInSlot(0).isEmpty())
				popResource(level, pos, itemDispatcher.getUpgrade().getStackInSlot(0));
			for (AbstractTransfer transfer : itemDispatcher.getTransfers()) {
				if(transfer instanceof ItemTransfer itemTransfer) {
					Containers.dropItemStack(level, pos.getX() + transfer.current.x, pos.getY() + transfer.current.y, pos.getZ() + transfer.current.z, itemTransfer.stack);
				}
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof AbstractDispatcherBE && !level.isClientSide && !player.isShiftKeyDown()) {
			NetworkHooks.openGui((ServerPlayer) player, (AbstractDispatcherBE) blockEntity, pos);
		}
		return super.use(state, level, pos, player, handIn, hit);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemDispatcherBE(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createDispatcherTicker(level, type, TransprotwoRegistry.DISPATCHER_BLOCK_ENTITY.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createDispatcherTicker(Level level, BlockEntityType<T> p_151989_, BlockEntityType<? extends ItemDispatcherBE> p_151990_) {
		return level.isClientSide ? createTickerHelper(p_151989_, p_151990_, ItemDispatcherBE::clientTick) : createTickerHelper(p_151989_, p_151990_, ItemDispatcherBE::serverTick);
	}
}
