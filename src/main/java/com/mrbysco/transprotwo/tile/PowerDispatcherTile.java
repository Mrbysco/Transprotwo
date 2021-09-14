package com.mrbysco.transprotwo.tile;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.screen.PowerDispatcherContainer;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.TransferParticleMessage;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.power.PowerStack;
import com.mrbysco.transprotwo.tile.transfer.power.PowerTransfer;
import com.mrbysco.transprotwo.util.DistanceHelper;
import com.mrbysco.transprotwo.util.PowerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PowerDispatcherTile extends AbstractDispatcherTile {
	private int line1 = 0x6b0e0e;
	private int line2 = 0x870707;
	private int line3 = 0xa10d0d;
	private int line4 = 0x870707;
	private int line5 = 0x640707;
	private Color[] colors = null;

	public PowerDispatcherTile() {
		super(TransprotwoRegistry.POWER_DISPATCHER_TILE.get());
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(Transprotwo.MOD_ID + ".container.power_dispatcher");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new PowerDispatcherContainer(id, playerInv, this);
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		ListNBT transferList = compound.getList("transfers", 10);
		this.transfers = Sets.newHashSet();
		for (int i = 0; i < transferList.size(); i++)
			this.transfers.add(PowerTransfer.loadFromNBT(transferList.getCompound(i)));

		this.line1 = compound.getInt("line1");
		this.line2 = compound.getInt("line2");
		this.line3 = compound.getInt("line3");
		this.line4 = compound.getInt("line4");
		this.line5 = compound.getInt("line5");

		super.read(state, compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("line1", this.line1);
		compound.putInt("line2", this.line2);
		compound.putInt("line3", this.line3);
		compound.putInt("line4", this.line4);
		compound.putInt("line5", this.line5);

		return super.write(compound);
	}

	void moveItems() {
		for (AbstractTransfer tr : getTransfers()) {
			if (!tr.blocked && world.isAreaLoaded(tr.rec.getLeft(), 1)) {
				tr.prev = new Vector3d(tr.current.x, tr.current.y, tr.current.z);
				tr.current = tr.current.add(tr.getVec().scale(getSpeed() / tr.getVec().length()));
			}
		}
	}

	@Override
	protected boolean startTransfer() {
		if (world.getGameTime() % getFrequence() == 0 && !world.isBlockPowered(pos)) {
			IEnergyStorage originHandler = getOriginHandler();
			if (originHandler == null)
				return false;
			List<Pair<BlockPos, Direction>> lis = Lists.newArrayList();
			for (Pair<BlockPos, Direction> pp : targets)
				if (wayFree(pos, pp.getLeft()))
					lis.add(pp);
			if (lis.isEmpty())
				return false;
			switch (mode) {
				case FF:
					lis.sort((o1, o2) -> {
						double dis1 = DistanceHelper.getDistance(pos, o2.getLeft());
						double dis2 = DistanceHelper.getDistance(pos, o1.getLeft());
						return Double.compare(dis1, dis2);
					});
					break;
				case NF:
					lis.sort((o1, o2) -> {
						double dis1 = DistanceHelper.getDistance(pos, o2.getLeft());
						double dis2 = DistanceHelper.getDistance(pos, o1.getLeft());
						return Double.compare(dis2, dis1);
					});
					break;
				case RA:
					Collections.shuffle(lis);
					break;
				case RR:
					if (lastInsertIndex + 1 >= lis.size())
						lastInsertIndex = 0;
					else
						lastInsertIndex++;
					List<Pair<BlockPos, Direction>> k = Lists.newArrayList();
					for (int i = 0; i < lis.size(); i++) {
						k.add(lis.get((lastInsertIndex + i) % lis.size()));
					}
					lis = Lists.newArrayList(k);
					break;
				default:
					break;
			}
			for (Pair<BlockPos, Direction> pair : lis) {
				if (originHandler.getEnergyStored() == 0)
					continue;
				int max = getStackSize() * 1000;
				PowerStack send = new PowerStack(max);
				boolean blocked = false;
				for (AbstractTransfer t : transfers) {
					if (t.rec.equals(pair) && t.blocked) {
						blocked = true;
						break;
					}
				}
				if (blocked)
					continue;

				IEnergyStorage dest = PowerUtil.getEnergyStorage(world.getTileEntity(pair.getLeft()), pair.getRight());
				int canInsert = PowerUtil.canInsert(dest, send);
				if (canInsert <= 0)
					continue;

				PowerStack x = new PowerStack(originHandler.extractEnergy(send.getPower(), true));
				if (!x.isEmpty()) {
					PowerTransfer tr = new PowerTransfer(pos, pair.getLeft(), pair.getRight(), x);
					if (!wayFree(tr.dis, tr.rec.getLeft()))
						continue;
					if (true) {
						Vector3d vec = tr.getVec().normalize().scale(0.015);
						CompoundNBT nbt = new CompoundNBT();
						nbt.putLong("pos", pos.toLong());
						nbt.putDouble("x", vec.x);
						nbt.putDouble("y", vec.y);
						nbt.putDouble("z", vec.z);


						this.summonParticles(nbt);
					}
					transfers.add(tr);
					originHandler.extractEnergy(send.getPower(), false);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void summonParticles(CompoundNBT nbt) {
		PacketHandler.sendToNearbyPlayers(new TransferParticleMessage(nbt), getPos(), 32, this.getWorld().getDimensionKey());
	}

	@Override
	public void tick() {
		moveItems();
		if (world.isRemote)
			return;
		boolean needSync = false;
		Iterator<Pair<BlockPos, Direction>> ite = targets.iterator();
		while (ite.hasNext()) {
			Pair<BlockPos, Direction> pa = ite.next();
			if (!PowerUtil.hasEnergyStorage(world, pa.getLeft(), pa.getRight())) {
				ite.remove();
				needSync = true;
			}
		}

		IEnergyStorage originHandler = getOriginHandler();
		if (originHandler == null)
			return;

		Iterator<AbstractTransfer> it = transfers.iterator();
		while (it.hasNext()) {
			AbstractTransfer t = it.next();
			if(t instanceof PowerTransfer) {
				PowerTransfer tr = (PowerTransfer)t;
				BlockPos currentPos = new BlockPos(getPos().getX() + tr.current.x, getPos().getY() + tr.current.y, getPos().getZ() + tr.current.z);
				if (tr.rec == null || !PowerUtil.hasEnergyStorage(world, tr.rec.getLeft(), tr.rec.getRight()) || (!currentPos.equals(pos) && !currentPos.equals(tr.rec.getLeft()) && !world.isAirBlock(currentPos) && !throughBlocks())) {
					it.remove();
					needSync = true;
					continue;
				}
				boolean received = tr.rec.getLeft().equals(currentPos);
				if (received && world.isAreaLoaded(tr.rec.getLeft(), 1)) {
					PowerStack rest = PowerUtil.insert(world.getTileEntity(tr.rec.getLeft()), tr.powerStack, tr.rec.getRight());
					if (!rest.isEmpty()) {
						tr.powerStack = rest;
						for (AbstractTransfer at : transfers) {
							if (at.rec.equals(tr.rec)) {
								if (!at.blocked)
									needSync = true;
								at.blocked = true;
							}
						}
					} else {
						for (AbstractTransfer at : transfers) {
							if (at.rec.equals(tr.rec))
								at.blocked = false;
						}
						it.remove();
						needSync = true;
					}
					TileEntity tile = world.getTileEntity(tr.rec.getLeft());
					if(tile != null) {
						tile.markDirty();
					}
				}
			}
		}
		boolean started = startTransfer();
		if (needSync || started)
			refreshClient();
	}

	public IEnergyStorage getOriginHandler() {
		Direction face = world.getBlockState(pos).get(DirectionalBlock.FACING);
		if (!world.isAreaLoaded(pos.offset(face), 1) && world.getTileEntity(pos.offset(face)) == null)
			return null;
		return PowerUtil.getEnergyStorage(world.getTileEntity(pos.offset(face)), face.getOpposite());
	}

	public Color[] getColors() {
		resetOptions();
		if(colors == null) {
			colors = new Color[5];
			colors[0] = new Color((line1 >> 16) & 0xFF, (line1 >> 8) & 0xFF, line1 & 0xFF);
			colors[1] = new Color((line2 >> 16) & 0xFF, (line2 >> 8) & 0xFF, line2 & 0xFF);
			colors[2] = new Color((line3 >> 16) & 0xFF, (line3 >> 8) & 0xFF, line3 & 0xFF);
			colors[3] = new Color((line4 >> 16) & 0xFF, (line4 >> 8) & 0xFF, line4 & 0xFF);
			colors[4] = new Color((line5 >> 16) & 0xFF, (line5 >> 8) & 0xFF, line5 & 0xFF);
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

	public void setLine5(int line5) {
		this.line5 = line5;
	}

	public int getLine5() {
		return line5;
	}

	public void setLine4(int line4) {
		this.line4 = line4;
	}

	@Override
	public void resetOptions() {
		super.resetOptions();
		this.line1 = 0x55CDFC;//0x6b0e0e;
		this.line2 = 0xF7A8B8;//0x870707;
		this.line3 = 0xFFFFFF;//0xa10d0d;
		this.line4 = 0xF7A8B8;//0x870707;
		this.line5 = 0x55CDFC;//0x640707;
		this.colors = null;
	}
}
