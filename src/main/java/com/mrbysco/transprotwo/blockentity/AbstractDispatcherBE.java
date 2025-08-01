package com.mrbysco.transprotwo.blockentity;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.FluidTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.ItemTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.power.PowerTransfer;
import com.mrbysco.transprotwo.item.UpgradeItem;
import com.mrbysco.transprotwo.util.Boost;
import com.mrbysco.transprotwo.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueInput.TypedInputList;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueOutput.TypedOutputList;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractDispatcherBE extends BlockEntity implements MenuProvider {
	protected Set<Pair<BlockPos, Direction>> targets = Sets.newHashSet();
	protected Set<AbstractTransfer> transfers = Sets.newHashSet();
	protected Mode mode = Mode.NF;
	protected int lastInsertIndex = 0;

	protected final ItemStackHandler upgradeHandler = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return stack.getItem() instanceof UpgradeItem;
		}

		@Override
		protected int getStackLimit(int slot, @NotNull ItemStack stack) {
			return 1;
		}
	};

	public AbstractDispatcherBE(BlockEntityType<? extends AbstractDispatcherBE> blockEntityType, BlockPos pos, BlockState state) {
		super(blockEntityType, pos, state);
	}

	public enum Mode {
		NF(0, "Nearest first"), FF(1, "Farthest first"), RA(2, "Random"), RR(3, "Round Robin");

		private final int id;
		private final String text;

		Mode(int id, String text) {
			this.id = id;
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public int getId() {
			return id;
		}

		public static Mode getByID(int ID) {
			return values()[ID];
		}

		public Mode next() {
			return values()[(this.ordinal() + 1) % values().length];
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);

		TypedInputList<Pair<Long, Direction>> targetList = input.listOrEmpty("targets",
				Codec.mapPair(Codec.LONG.fieldOf("pos"), Direction.CODEC.fieldOf("face")).codec());
		this.targets = Sets.newHashSet();
		for (Pair<Long, Direction> target : targetList) {
			BlockPos pos = BlockPos.of(target.getFirst());
			this.targets.add(Pair.of(pos, target.getSecond()));
		}

		Optional<String> modeOpt = input.getString("mode");
		this.mode = modeOpt.map(Mode::valueOf).orElse(Mode.NF);

		Optional<ValueInput> upgradeInput = input.child("upgrade");
		upgradeInput.ifPresent(this.upgradeHandler::deserialize);

		this.lastInsertIndex = input.getIntOr("index", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		TypedOutputList<ItemTransfer> itemTransfers = output.list("itemTransfers", ItemTransfer.CODEC);
		TypedOutputList<FluidTransfer> fluidTransfers = output.list("fluidTransfers", FluidTransfer.CODEC);
		TypedOutputList<PowerTransfer> powerTransfers = output.list("powerTransfers", PowerTransfer.CODEC);
		for (AbstractTransfer transfer : this.transfers) {
			if (transfer instanceof ItemTransfer itemTransfer) {
				itemTransfers.add(itemTransfer);
			} else if (transfer instanceof FluidTransfer fluidTransfer) {
				fluidTransfers.add(fluidTransfer);
			} else if (transfer instanceof PowerTransfer powerTransfer) {
				powerTransfers.add(powerTransfer);
			}
		}

		TypedOutputList<Pair<Long, Direction>> targetsList = output.list("targets", Codec.mapPair(Codec.LONG.fieldOf("pos"), Direction.CODEC.fieldOf("face")).codec());
		for (Pair<BlockPos, Direction> target : this.targets) {
			targetsList.add(Pair.of(target.getFirst().asLong(), target.getSecond()));
		}

		ValueOutput upgradeOutput = output.child("upgrade");
		upgradeHandler.serialize(upgradeOutput);

		output.putString("mode", this.mode.toString());
		output.putInt("index", this.lastInsertIndex);
	}

	public boolean wayFree(BlockPos start, BlockPos end) {
		if (throughBlocks())
			return true;
		Vec3 p1 = new Vec3(start.getX(), start.getY(), start.getZ()).add(.5, .5, .5);
		Vec3 p2 = new Vec3(end.getX(), end.getY(), end.getZ()).add(.5, .5, .5);
		Vec3 d = new Vec3(end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ());
		d = d.normalize().scale(0.25);
		Set<BlockPos> set = Sets.newHashSet();
		while (p1.distanceTo(p2) > 0.5) {
			set.add(BlockPos.containing(p1));
			p1 = p1.add(d);
		}
		set.remove(start);
		set.remove(end);
		for (BlockPos p : set)
			if (!level.isEmptyBlock(p))
				return false;
		return true;
	}

	boolean throughBlocks() {
		return !upgradeHandler.getStackInSlot(0).isEmpty() && (((UpgradeItem) upgradeHandler.getStackInSlot(0).getItem()).getUpgrade()) >= 2;
	}

	long getFrequence() {
		if (upgradeHandler.getStackInSlot(0).isEmpty() || !(upgradeHandler.getStackInSlot(0).getItem() instanceof UpgradeItem))
			return Boost.defaultFrequence;
		return (((UpgradeItem) upgradeHandler.getStackInSlot(0).getItem()).getBoost()).frequence();
	}

	double getSpeed() {
		if (upgradeHandler.getStackInSlot(0).isEmpty() || !(upgradeHandler.getStackInSlot(0).getItem() instanceof UpgradeItem))
			return Boost.defaultSpeed;
		return (((UpgradeItem) upgradeHandler.getStackInSlot(0).getItem()).getBoost()).speed();
	}

	int getStackSize() {
		if (upgradeHandler.getStackInSlot(0).isEmpty() || !(upgradeHandler.getStackInSlot(0).getItem() instanceof UpgradeItem))
			return Boost.defaultStackSize;
		return (((UpgradeItem) upgradeHandler.getStackInSlot(0).getItem()).getBoost()).stackSize();
	}

	protected boolean startTransfer() {
		return false;
	}

	public void summonParticles(CompoundTag nbt) {

	}

	public Color getColor() {
		return Color.getHSBColor(((worldPosition.hashCode() * 761) % 360L) / 360f, 1, 1);
	}

	public Set<AbstractTransfer> getTransfers() {
		return transfers;
	}

	public Set<Pair<BlockPos, Direction>> getTargets() {
		return targets;
	}

	public Mode getMode() {
		return mode;
	}

	public void cycleMode() {
		mode = mode.next();
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public ItemStackHandler getUpgrade() {
		return upgradeHandler;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ValueInput valueInput) {
		super.onDataPacket(net, valueInput);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		CompoundTag tag = new CompoundTag();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(Transprotwo.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, lookupProvider);
			this.saveAdditional(output);
			tag.merge(output.buildResult());
		}
		return tag;
	}

	@Override
	public CompoundTag getPersistentData() {
		CompoundTag tag = new CompoundTag();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(Transprotwo.LOGGER)) {
			HolderLookup.Provider lookupProvider = this.level != null ? this.level.registryAccess() : VanillaRegistries.createLookup();
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, lookupProvider);
			this.saveAdditional(output);
			tag.merge(output.buildResult());
		}
		return tag;
	}

	public boolean isUsableByPlayer(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	public void refreshClient() {
		setChanged();
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}

	public void resetOptions() {
		mode = Mode.NF;
	}
}
