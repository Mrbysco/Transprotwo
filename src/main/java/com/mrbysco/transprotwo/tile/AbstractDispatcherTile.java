package com.mrbysco.transprotwo.tile;

import com.google.common.collect.Sets;
import com.mrbysco.transprotwo.item.UpgradeItem;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.util.Boost;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.Set;

public abstract class AbstractDispatcherTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
	protected Set<Pair<BlockPos, Direction>> targets = Sets.newHashSet();
	protected Set<AbstractTransfer> transfers = Sets.newHashSet();
	protected Mode mode = Mode.NF;
	protected int lastInsertIndex = 0;

	protected final ItemStackHandler upgradeHandler = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return stack.getItem() instanceof UpgradeItem;
		}

		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			return 1;
		}
	};
	protected LazyOptional<IItemHandler> upgradeCap = LazyOptional.of(() -> upgradeHandler);

	public AbstractDispatcherTile(TileEntityType<? extends AbstractDispatcherTile> tileEntityTypeIn) {
		super(tileEntityTypeIn);
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
	public void load(BlockState state, CompoundNBT compound) {
		ListNBT targetList = compound.getList("targets", 10);
		this.targets = Sets.newHashSet();
		for (int i = 0; i < targetList.size(); i++) {
			CompoundNBT nbt = targetList.getCompound(i);
			this.targets.add(new ImmutablePair<BlockPos, Direction>(BlockPos.of(nbt.getLong("pos")), Direction.values()[nbt.getInt("face")]));
		}

		if (compound.contains("mode"))
			this.mode = Mode.valueOf(compound.getString("mode"));
		else
			this.mode = Mode.NF;

		this.upgradeHandler.deserializeNBT(compound.getCompound("upgrade"));
		this.lastInsertIndex = compound.getInt("index");

		super.load(state, compound);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		ListNBT transferList = new ListNBT();
		for (AbstractTransfer transfer : transfers) {
			CompoundNBT n = new CompoundNBT();
			transfer.writeToNBT(n);
			transferList.add(n);
		}
		compound.put("transfers", transferList);

		ListNBT targetList = new ListNBT();
		for (Pair<BlockPos, Direction> target : this.targets) {
			CompoundNBT tag = new CompoundNBT();
			tag.putLong("pos", target.getLeft().asLong());
			tag.putLong("face", target.getRight().ordinal());
			targetList.add(tag);
		}

		compound.put("upgrade", upgradeHandler.serializeNBT());

		compound.put("targets", targetList);
		compound.putString("mode", this.mode.toString());
		compound.putInt("index", this.lastInsertIndex);

		return super.save(compound);
	}

	public boolean wayFree(BlockPos start, BlockPos end) {
		if (throughBlocks())
			return true;
		Vector3d p1 = new Vector3d(start.getX(), start.getY(), start.getZ()).add(.5, .5, .5);
		Vector3d p2 = new Vector3d(end.getX(), end.getY(), end.getZ()).add(.5, .5, .5);
		Vector3d d = new Vector3d(end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ());
		d = d.normalize().scale(0.25);
		Set<BlockPos> set = Sets.newHashSet();
		while (p1.distanceTo(p2) > 0.5) {
			set.add(new BlockPos(p1));
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
		return (((UpgradeItem) upgradeHandler.getStackInSlot(0).getItem()).getBoost()).frequence;
	}

	double getSpeed() {
		if (upgradeHandler.getStackInSlot(0).isEmpty() || !(upgradeHandler.getStackInSlot(0).getItem() instanceof UpgradeItem))
			return Boost.defaultSpeed;
		return (((UpgradeItem) upgradeHandler.getStackInSlot(0).getItem()).getBoost()).speed;
	}

	int getStackSize() {
		if (upgradeHandler.getStackInSlot(0).isEmpty() || !(upgradeHandler.getStackInSlot(0).getItem() instanceof UpgradeItem))
			return Boost.defaultStackSize;
		return (((UpgradeItem) upgradeHandler.getStackInSlot(0).getItem()).getBoost()).stackSize;
	}

	protected boolean startTransfer() {
		return false;
	}

	public void summonParticles(CompoundNBT nbt) {

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
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.load(getBlockState(), packet.getTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		this.save(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		this.load(state, tag);
	}

	@Override
	public CompoundNBT getTileData() {
		CompoundNBT nbt = new CompoundNBT();
		this.save(nbt);
		return nbt;
	}

	public boolean isUsableByPlayer(PlayerEntity player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
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

	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		upgradeCap.invalidate();
	}
}
