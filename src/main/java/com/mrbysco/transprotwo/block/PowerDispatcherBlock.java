package com.mrbysco.transprotwo.block;

import com.mrbysco.transprotwo.tile.PowerDispatcherTile;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.power.PowerTransfer;
import com.mrbysco.transprotwo.util.PowerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
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
import java.util.UUID;

public class PowerDispatcherBlock extends AbstractDispatcherBlock {

	public PowerDispatcherBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getBlockEntity(pos);
		if (!worldIn.isClientSide && tile instanceof PowerDispatcherTile) {
			PowerDispatcherTile dispatcherTile = (PowerDispatcherTile) tile;
			IEnergyStorage originHandler = getOriginHandler(state, worldIn, pos);
			if (!dispatcherTile.getUpgrade().getStackInSlot(0).isEmpty())
				popResource(worldIn, pos, dispatcherTile.getUpgrade().getStackInSlot(0));
			for (AbstractTransfer abstractTransfer : dispatcherTile.getTransfers()) {
				if(abstractTransfer instanceof PowerTransfer) {
					PowerTransfer transfer = (PowerTransfer) abstractTransfer;
					originHandler.receiveEnergy(transfer.powerStack.getAmount(), false);
				}
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	public IEnergyStorage getOriginHandler(BlockState state, World world, BlockPos pos) {
		Direction face = state.getValue(DirectionalBlock.FACING);
		if (!world.isAreaLoaded(pos.relative(face), 1) && world.getBlockEntity(pos.relative(face)) == null)
			return null;
		return PowerUtil.getEnergyStorage(world.getBlockEntity(pos.relative(face)), face.getOpposite());
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getBlockEntity(pos);
		if(tile instanceof PowerDispatcherTile && !worldIn.isClientSide && !player.isShiftKeyDown()) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (PowerDispatcherTile) tile, pos);
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if(placer instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)placer;
			//Shy wanted to always have it default to the trans colors when she placed it <3
			String shyUUID = "7135da42-d327-47bb-bb04-5ba4e212fb32";
			boolean flag = player.getGameProfile().isComplete() && player.getGameProfile().getId().equals(UUID.fromString(shyUUID));
			if(flag) {
				TileEntity tile = worldIn.getBlockEntity(pos);
				if(tile instanceof PowerDispatcherTile) {
					PowerDispatcherTile dispatcherTile = (PowerDispatcherTile) tile;
					dispatcherTile.setLine1(0x55CDFC);
					dispatcherTile.setLine2(0xF7A8B8);
					dispatcherTile.setLine3(0xFFFFFF);
					dispatcherTile.setLine4(0xF7A8B8);
					dispatcherTile.setLine5(0x55CDFC);
					dispatcherTile.initializeColors();
				}
			}
		}
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
