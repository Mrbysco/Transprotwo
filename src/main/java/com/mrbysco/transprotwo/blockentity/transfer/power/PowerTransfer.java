package com.mrbysco.transprotwo.blockentity.transfer.power;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class PowerTransfer extends AbstractTransfer {
	public static final Codec<PowerTransfer> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							BlockPos.CODEC.fieldOf("dis").forGetter(transfer -> transfer.dis),
							Codec.pair(
									BlockPos.CODEC, Direction.CODEC
							).fieldOf("rec").forGetter(transfer -> transfer.rec),
							Vec3.CODEC.optionalFieldOf("current", new Vec3(.5, .5, .5)).forGetter(transfer -> transfer.current),
							Codec.BOOL.optionalFieldOf("blocked", false).forGetter(transfer -> transfer.blocked),
							Codec.INT.optionalFieldOf("turn", new Random().nextInt()).forGetter(transfer -> transfer.turn),
							PowerStack.CODEC.fieldOf("fluidstack").forGetter(transfer -> transfer.powerStack)
					)
					.apply(instance, PowerTransfer::new)
	);
	public PowerStack powerStack;

	public PowerTransfer(BlockPos dis, Pair<BlockPos, Direction> rec, Vec3 current, boolean blocked, int turn,
	                    PowerStack stack) {
		super(dis, rec, current, blocked, turn);
		this.powerStack = stack;
	}

	public PowerTransfer(BlockPos worldPosition, BlockPos rec, Direction face, PowerStack stack) {
		this(worldPosition, Pair.of(rec, face), new Vec3(.5, .5, .5), false, new Random().nextInt(), stack);
	}

	private PowerTransfer() {
		this(BlockPos.ZERO, new Pair<>(BlockPos.ZERO, Direction.DOWN), new Vec3(.5, .5, .5), false, new Random().nextInt(), PowerStack.EMPTY);
	}
}
