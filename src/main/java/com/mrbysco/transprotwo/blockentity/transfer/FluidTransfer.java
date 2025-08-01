package com.mrbysco.transprotwo.blockentity.transfer;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Random;

public class FluidTransfer extends AbstractTransfer {
	public static final Codec<FluidTransfer> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							BlockPos.CODEC.fieldOf("dis").forGetter(transfer -> transfer.dis),
							Codec.pair(
									BlockPos.CODEC, Direction.CODEC
							).fieldOf("rec").forGetter(transfer -> transfer.rec),
							Vec3.CODEC.optionalFieldOf("current", new Vec3(.5, .5, .5)).forGetter(transfer -> transfer.current),
							Codec.BOOL.optionalFieldOf("blocked", false).forGetter(transfer -> transfer.blocked),
							Codec.INT.optionalFieldOf("turn", new Random().nextInt()).forGetter(transfer -> transfer.turn),
							FluidStack.OPTIONAL_CODEC.fieldOf("fluidstack").forGetter(transfer -> transfer.fluidStack)
					)
					.apply(instance, FluidTransfer::new)
	);
	public FluidStack fluidStack;

	public FluidTransfer(BlockPos dis, Pair<BlockPos, Direction> rec, Vec3 current, boolean blocked, int turn,
	                     FluidStack stack) {
		super(dis, rec, current, blocked, turn);
		this.fluidStack = stack;
	}

	public FluidTransfer(BlockPos worldPosition, BlockPos rec, Direction face, FluidStack stack) {
		this(worldPosition, Pair.of(rec, face), new Vec3(.5, .5, .5), false, new Random().nextInt(), stack);
	}

	private FluidTransfer() {
		this(BlockPos.ZERO, new Pair<>(BlockPos.ZERO, Direction.DOWN), new Vec3(.5, .5, .5), false, new Random().nextInt(), FluidStack.EMPTY);
	}
}
