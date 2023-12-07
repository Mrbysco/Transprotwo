package com.mrbysco.transprotwo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public abstract class AbstractDispatcherBlock extends DirectionalBlock implements EntityBlock {
	protected static final VoxelShape NORTH_SHAPE = Block.box(1, 1, 0, 15, 15, 8);
	protected static final VoxelShape SOUTH_SHAPE = Block.box(1, 1, 8, 15, 15, 16);
	protected static final VoxelShape WEST_SHAPE = Block.box(0, 1, 1, 8, 15, 15);
	protected static final VoxelShape EAST_SHAPE = Block.box(8, 1, 1, 16, 15, 15);
	protected static final VoxelShape DOWN_SHAPE = Block.box(1, 0, 1, 15, 8, 15);
	protected static final VoxelShape TOP_SHAPE = Block.box(1, 8, 1, 15, 16, 15);

	public AbstractDispatcherBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			default -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case DOWN -> DOWN_SHAPE;
			case EAST -> EAST_SHAPE;
			case UP -> TOP_SHAPE;
			case WEST -> WEST_SHAPE;
		};
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int p_49229_, int p_49230_) {
		super.triggerEvent(state, level, pos, p_49229_, p_49230_);
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity == null ? false : blockentity.triggerEvent(p_49229_, p_49230_);
	}

	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
		return p_152134_ == p_152133_ ? (BlockEntityTicker<A>) p_152135_ : null;
	}
}
