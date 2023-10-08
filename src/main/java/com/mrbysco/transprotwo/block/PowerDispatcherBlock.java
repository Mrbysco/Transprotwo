package com.mrbysco.transprotwo.block;

import com.mrbysco.transprotwo.blockentity.PowerDispatcherBE;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.power.PowerTransfer;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.util.PowerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class PowerDispatcherBlock extends AbstractDispatcherBlock {

	public PowerDispatcherBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!level.isClientSide && blockEntity instanceof PowerDispatcherBE powerDispatcher) {
			IEnergyStorage originHandler = getOriginHandler(state, level, pos);
			if (!powerDispatcher.getUpgrade().getStackInSlot(0).isEmpty())
				popResource(level, pos, powerDispatcher.getUpgrade().getStackInSlot(0));
			for (AbstractTransfer abstractTransfer : powerDispatcher.getTransfers()) {
				if (abstractTransfer instanceof PowerTransfer transfer) {
					originHandler.receiveEnergy(transfer.powerStack.getAmount(), false);
				}
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	public IEnergyStorage getOriginHandler(BlockState state, Level level, BlockPos pos) {
		Direction face = state.getValue(DirectionalBlock.FACING);
		if (!level.isAreaLoaded(pos.relative(face), 1) && level.getBlockEntity(pos.relative(face)) == null)
			return null;
		return PowerUtil.getEnergyStorage(level.getBlockEntity(pos.relative(face)), face.getOpposite());
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof PowerDispatcherBE && !level.isClientSide && !player.isShiftKeyDown()) {
			NetworkHooks.openScreen((ServerPlayer) player, (PowerDispatcherBE) blockEntity, pos);
		}
		return super.use(state, level, pos, player, handIn, hit);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player) {
			//Shy wanted to always have it default to the trans colors when she placed it <3
			String shyUUID = "7135da42-d327-47bb-bb04-5ba4e212fb32";
			boolean flag = player.getGameProfile().isComplete() && player.getGameProfile().getId().equals(UUID.fromString(shyUUID));
			if (flag) {
				BlockEntity blockEntity = level.getBlockEntity(pos);
				if (blockEntity instanceof PowerDispatcherBE powerDispatcher) {
					powerDispatcher.setLine1(0x55CDFC);
					powerDispatcher.setLine2(0xF7A8B8);
					powerDispatcher.setLine3(0xFFFFFF);
					powerDispatcher.setLine4(0xF7A8B8);
					powerDispatcher.setLine5(0x55CDFC);
					powerDispatcher.initializeColors();
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PowerDispatcherBE(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createDispatcherTicker(level, type, TransprotwoRegistry.POWER_DISPATCHER_BLOCK_ENTITY.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createDispatcherTicker(Level level, BlockEntityType<T> p_151989_, BlockEntityType<? extends PowerDispatcherBE> p_151990_) {
		return level.isClientSide ? createTickerHelper(p_151989_, p_151990_, PowerDispatcherBE::clientTick) : createTickerHelper(p_151989_, p_151990_, PowerDispatcherBE::serverTick);
	}
}
