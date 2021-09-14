package com.mrbysco.transprotwo.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public abstract class AbstractDispatcherBlock extends DirectionalBlock {
	protected static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(1, 1, 0, 15, 15, 8);
	protected static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(1, 1, 8, 15, 15, 16);
	protected static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(0, 1, 1, 8, 15, 15);
	protected static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(8, 1, 1, 16, 15, 15);
	protected static final VoxelShape DOWN_SHAPE = Block.makeCuboidShape(1, 0, 1, 15, 8, 15);
	protected static final VoxelShape TOP_SHAPE = Block.makeCuboidShape(1, 8, 1, 15, 16, 15);

	public AbstractDispatcherBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.DOWN));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getFace().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(FACING)) {
			default:
				return NORTH_SHAPE;
			case SOUTH:
				return SOUTH_SHAPE;
			case DOWN:
				return DOWN_SHAPE;
			case EAST:
				return EAST_SHAPE;
			case UP:
				return TOP_SHAPE;
			case WEST:
				return WEST_SHAPE;
		}
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
