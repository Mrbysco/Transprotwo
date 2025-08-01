package com.mrbysco.transprotwo.blockentity.transfer;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ItemTransfer extends AbstractTransfer {
	public static final Codec<ItemTransfer> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							BlockPos.CODEC.fieldOf("dis").forGetter(transfer -> transfer.dis),
							Codec.mapPair(
									BlockPos.CODEC.fieldOf("rec"), Direction.CODEC.fieldOf("face")
							).fieldOf("rec").forGetter(transfer -> transfer.rec),
							Vec3.CODEC.optionalFieldOf("current", new Vec3(.5, .5, .5)).forGetter(transfer -> transfer.current),
							Codec.BOOL.optionalFieldOf("blocked", false).forGetter(transfer -> transfer.blocked),
							Codec.INT.optionalFieldOf("turn", new Random().nextInt()).forGetter(transfer -> transfer.turn),
							ItemStack.OPTIONAL_CODEC.fieldOf("stack").forGetter(transfer -> transfer.stack)
					)
					.apply(instance, ItemTransfer::new)
	);

	public ItemStack stack;


	public ItemTransfer(BlockPos dis, Pair<BlockPos, Direction> rec, Vec3 current, boolean blocked, int turn,
	                    ItemStack stack) {
		super(dis, rec, current, blocked, turn);
		this.stack = stack;
	}

	public ItemTransfer(BlockPos worldPosition, BlockPos rec, Direction face, ItemStack stack) {
		this(worldPosition, Pair.of(rec, face), new Vec3(.5, .5, .5), false, new Random().nextInt(), stack);
	}

	private ItemTransfer() {
		this(BlockPos.ZERO, new Pair<>(BlockPos.ZERO, Direction.DOWN), new Vec3(.5, .5, .5), false, new Random().nextInt(), ItemStack.EMPTY);
	}
}
