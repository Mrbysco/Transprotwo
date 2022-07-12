package com.mrbysco.transprotwo.blockentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.power.PowerStack;
import com.mrbysco.transprotwo.blockentity.transfer.power.PowerTransfer;
import com.mrbysco.transprotwo.client.screen.PowerDispatcherContainer;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.TransferParticleMessage;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.util.DistanceHelper;
import com.mrbysco.transprotwo.util.PowerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PowerDispatcherBE extends AbstractDispatcherBE {
	private int line1 = 0x6b0e0e;
	private int line2 = 0x870707;
	private int line3 = 0xa10d0d;
	private int line4 = 0x870707;
	private int line5 = 0x640707;
	private Color[] colors = null;

	public PowerDispatcherBE(BlockPos pos, BlockState state) {
		super(TransprotwoRegistry.POWER_DISPATCHER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent(Transprotwo.MOD_ID + ".container.power_dispatcher");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player playerEntity) {
		return new PowerDispatcherContainer(id, playerInv, this);
	}

	@Override
	public void load(CompoundTag compound) {
		ListTag transferList = compound.getList("transfers", 10);
		this.transfers = Sets.newHashSet();
		for (int i = 0; i < transferList.size(); i++)
			this.transfers.add(PowerTransfer.loadFromNBT(transferList.getCompound(i)));

		this.line1 = compound.getInt("line1");
		this.line2 = compound.getInt("line2");
		this.line3 = compound.getInt("line3");
		this.line4 = compound.getInt("line4");
		this.line5 = compound.getInt("line5");

		super.load(compound);
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putInt("line1", this.line1);
		compound.putInt("line2", this.line2);
		compound.putInt("line3", this.line3);
		compound.putInt("line4", this.line4);
		compound.putInt("line5", this.line5);
	}

	void moveItems() {
		for (AbstractTransfer tr : getTransfers()) {
			if (!tr.blocked && level.isAreaLoaded(tr.rec.getLeft(), 1)) {
				tr.prev = new Vec3(tr.current.x, tr.current.y, tr.current.z);
				tr.current = tr.current.add(tr.getVec().scale(getSpeed() / tr.getVec().length()));
			}
		}
	}

	@Override
	protected boolean startTransfer() {
		if (level.getGameTime() % getFrequence() == 0 && !level.hasNeighborSignal(worldPosition)) {
			IEnergyStorage originHandler = getOriginHandler();
			if (originHandler == null)
				return false;
			List<Pair<BlockPos, Direction>> lis = Lists.newArrayList();
			for (Pair<BlockPos, Direction> pp : targets)
				if (wayFree(worldPosition, pp.getLeft()))
					lis.add(pp);
			if (lis.isEmpty())
				return false;
			switch (mode) {
				case FF -> lis.sort((o1, o2) -> {
					double dis1 = DistanceHelper.getDistance(worldPosition, o2.getLeft());
					double dis2 = DistanceHelper.getDistance(worldPosition, o1.getLeft());
					return Double.compare(dis1, dis2);
				});
				case NF -> lis.sort((o1, o2) -> {
					double dis1 = DistanceHelper.getDistance(worldPosition, o2.getLeft());
					double dis2 = DistanceHelper.getDistance(worldPosition, o1.getLeft());
					return Double.compare(dis2, dis1);
				});
				case RA -> Collections.shuffle(lis);
				case RR -> {
					if (lastInsertIndex + 1 >= lis.size())
						lastInsertIndex = 0;
					else
						lastInsertIndex++;
					List<Pair<BlockPos, Direction>> k = Lists.newArrayList();
					for (int i = 0; i < lis.size(); i++) {
						k.add(lis.get((lastInsertIndex + i) % lis.size()));
					}
					lis = Lists.newArrayList(k);
				}
				default -> {
				}
			}
			for (Pair<BlockPos, Direction> pair : lis) {
				if (originHandler.getEnergyStored() == 0)
					continue;
				int max = getStackSize();
				PowerStack send = new PowerStack(max * 1000);
				boolean blocked = false;
				for (AbstractTransfer t : transfers) {
					if (t.rec.equals(pair) && t.blocked) {
						blocked = true;
						break;
					}
				}
				if (blocked)
					continue;

				IEnergyStorage dest = PowerUtil.getEnergyStorage(level.getBlockEntity(pair.getLeft()), pair.getRight());
				int canInsert = !dest.canReceive() ? 0 : PowerUtil.canInsert(dest, send);
				if (canInsert <= 0)
					continue;

				PowerStack x = new PowerStack(originHandler.extractEnergy(send.getAmount(), true));
				if (!x.isEmpty()) {
					PowerTransfer tr = new PowerTransfer(worldPosition, pair.getLeft(), pair.getRight(), x);
					if (!wayFree(tr.dis, tr.rec.getLeft()))
						continue;
					if (true) {
						Vec3 vec = tr.getVec().normalize().scale(0.015);
						CompoundTag nbt = new CompoundTag();
						nbt.putLong("pos", worldPosition.asLong());
						nbt.putDouble("x", vec.x);
						nbt.putDouble("y", vec.y);
						nbt.putDouble("z", vec.z);


						this.summonParticles(nbt);
					}
					transfers.add(tr);
					originHandler.extractEnergy(send.getAmount(), false);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void summonParticles(CompoundTag nbt) {
		PacketHandler.sendToNearbyPlayers(new TransferParticleMessage(nbt), getBlockPos(), 32, this.getLevel().dimension());
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, PowerDispatcherBE powerDispatcher) {
		powerDispatcher.moveItems();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, PowerDispatcherBE powerDispatcher) {
		powerDispatcher.moveItems();
		boolean needSync = false;
		Iterator<Pair<BlockPos, Direction>> ite = powerDispatcher.targets.iterator();
		while (ite.hasNext()) {
			Pair<BlockPos, Direction> pa = ite.next();
			if (!PowerUtil.hasEnergyStorage(level, pa.getLeft(), pa.getRight())) {
				ite.remove();
				needSync = true;
			}
		}

		IEnergyStorage originHandler = powerDispatcher.getOriginHandler();
		if (originHandler == null)
			return;

		Iterator<AbstractTransfer> it = powerDispatcher.transfers.iterator();
		while (it.hasNext()) {
			AbstractTransfer t = it.next();
			if (t instanceof PowerTransfer tr) {
				BlockPos currentPos = new BlockPos(pos.getX() + tr.current.x, pos.getY() + tr.current.y, pos.getZ() + tr.current.z);
				if (tr.rec == null || !PowerUtil.hasEnergyStorage(level, tr.rec.getLeft(), tr.rec.getRight()) ||
						(!currentPos.equals(pos) && !currentPos.equals(tr.rec.getLeft()) && !level.isEmptyBlock(currentPos) && !powerDispatcher.throughBlocks())) {
					it.remove();
					needSync = true;
					continue;
				}
				boolean received = tr.rec.getLeft().equals(currentPos);
				if (received && level.isAreaLoaded(tr.rec.getLeft(), 1)) {
					PowerStack rest = PowerUtil.insert(level.getBlockEntity(tr.rec.getLeft()), tr.powerStack, tr.rec.getRight());
					if (!rest.isEmpty()) {
						tr.powerStack = rest;
						for (AbstractTransfer at : powerDispatcher.transfers) {
							if (at.rec.equals(tr.rec)) {
								if (!at.blocked)
									needSync = true;
								at.blocked = true;
							}
						}
					} else {
						for (AbstractTransfer at : powerDispatcher.transfers) {
							if (at.rec.equals(tr.rec))
								at.blocked = false;
						}
						it.remove();
						needSync = true;
					}
					BlockEntity blockEntity = level.getBlockEntity(tr.rec.getLeft());
					if (blockEntity != null) {
						blockEntity.setChanged();
					}
				}
			}
		}
		boolean started = powerDispatcher.startTransfer();
		if (needSync || started)
			powerDispatcher.refreshClient();
	}

	public IEnergyStorage getOriginHandler() {
		Direction face = level.getBlockState(worldPosition).getValue(DirectionalBlock.FACING);
		if (!level.isAreaLoaded(worldPosition.relative(face), 1) && level.getBlockEntity(worldPosition.relative(face)) == null)
			return null;
		return PowerUtil.getEnergyStorage(level.getBlockEntity(worldPosition.relative(face)), face.getOpposite());
	}

	public Color[] getColors() {
		if (colors == null) {
			initializeColors();
		}

		return colors;
	}

	public int getLine1() {
		return line1;
	}

	public void setLine1(int line1) {
		this.line1 = line1;
	}

	public int getLine2() {
		return line2;
	}

	public void setLine2(int line2) {
		this.line2 = line2;
	}

	public int getLine3() {
		return line3;
	}

	public void setLine3(int line3) {
		this.line3 = line3;
	}

	public int getLine4() {
		return line4;
	}

	public void setLine4(int line4) {
		this.line4 = line4;
	}

	public int getLine5() {
		return line5;
	}

	public void setLine5(int line5) {
		this.line5 = line5;
	}

	@Override
	public void resetOptions() {
		super.resetOptions();
		this.line1 = 0x6b0e0e;
		this.line2 = 0x870707;
		this.line3 = 0xa10d0d;
		this.line4 = 0x870707;
		this.line5 = 0x640707;
	}

	public void initializeColors() {
		this.colors = new Color[5];
		this.colors[0] = new Color((this.line1 >> 16) & 0xFF, (this.line1 >> 8) & 0xFF, this.line1 & 0xFF);
		this.colors[1] = new Color((this.line2 >> 16) & 0xFF, (this.line2 >> 8) & 0xFF, this.line2 & 0xFF);
		this.colors[2] = new Color((this.line3 >> 16) & 0xFF, (this.line3 >> 8) & 0xFF, this.line3 & 0xFF);
		this.colors[3] = new Color((this.line4 >> 16) & 0xFF, (this.line4 >> 8) & 0xFF, this.line4 & 0xFF);
		this.colors[4] = new Color((this.line5 >> 16) & 0xFF, (this.line5 >> 8) & 0xFF, this.line5 & 0xFF);
	}

	@Override
	public void refreshClient() {
		super.refreshClient();
	}
}
