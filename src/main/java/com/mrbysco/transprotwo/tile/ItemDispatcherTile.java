package com.mrbysco.transprotwo.tile;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.screen.DispatcherContainer;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.TransferParticleMessage;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.ItemTransfer;
import com.mrbysco.transprotwo.util.DistanceHelper;
import com.mrbysco.transprotwo.util.InventoryUtil;
import com.mrbysco.transprotwo.util.StackHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ItemDispatcherTile extends AbstractDispatcherTile {
	private boolean tag = false;
	private boolean durability = true;
	private boolean nbt = false;
	private boolean white = false;
	private boolean mod = false;

	public final ItemStackHandler filterHandler = new ItemStackHandler(9);
	private LazyOptional<IItemHandler> filterCap = LazyOptional.of(() -> filterHandler);

	private int stockNum = 0;

	public ItemDispatcherTile() {
		super(TransprotwoRegistry.DISPATCHER_TILE.get());
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(Transprotwo.MOD_ID + ".container.dispatcher");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new DispatcherContainer(id, playerInv, this);
	}

	public boolean canTransfer(ItemStack stack) {
		if (stack.isEmpty())
			return false;
		for (int i = 0; i < filterHandler.getSlots(); i++) {
			ItemStack s = filterHandler.getStackInSlot(i);
			if (!s.isEmpty() && equal(stack, s))
				return white;
		}
		return !white;
	}

	public boolean equal(ItemStack stack1, ItemStack stack2) {
		if (stack1.isEmpty() || stack2.isEmpty())
			return false;
		if (tag && StackHelper.matchAnyTag(stack1, stack2))
			return true;
		if (mod && stack1.getItem().getRegistryName().getNamespace().equals(stack2.getItem().getRegistryName().getNamespace()))
			return true;
		if (nbt && !ItemStack.areItemStackTagsEqual(stack1, stack2))
			return false;
		if (durability && stack1.getDamage() == stack2.getDamage())
			return true;
		return stack1.getItem() == stack2.getItem();
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		ListNBT transferList = compound.getList("transfers", 10);
		this.transfers = Sets.newHashSet();
		for (int i = 0; i < transferList.size(); i++)
			this.transfers.add(ItemTransfer.loadFromNBT(transferList.getCompound(i)));

		this.filterHandler.deserializeNBT(compound.getCompound("filter"));

		tag = compound.getBoolean("tag");
		durability = compound.getBoolean("durability");
		nbt = compound.getBoolean("nbt");
		white = compound.getBoolean("white");
		mod = compound.getBoolean("mod");
		stockNum = compound.getInt("stock");
		super.read(state, compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("filter", filterHandler.serializeNBT());

		compound.putBoolean("tag", tag);
		compound.putBoolean("durability", durability);
		compound.putBoolean("nbt", nbt);
		compound.putBoolean("white", white);
		compound.putBoolean("mod", mod);
		compound.putInt("stock", stockNum);
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
			Direction face = world.getBlockState(pos).get(DirectionalBlock.FACING);
			if (!world.isAreaLoaded(pos.offset(face),1))
				return false;
			IItemHandler inv = InventoryUtil.getItemHandler(world.getTileEntity(pos.offset(face)), face.getOpposite());
			if (inv == null)
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
			for (Pair<BlockPos, Direction> pair : lis)
				for (int i = 0; i < inv.getSlots(); i++) {
					if (inv.getStackInSlot(i).isEmpty() || !canTransfer(inv.getStackInSlot(i)))
						continue;
					int max = getStackSize();
					ItemStack send = inv.extractItem(i, max, true);
					boolean blocked = false;
					for (AbstractTransfer t : transfers) {
						if (t.rec.equals(pair) && t.blocked) {
							blocked = true;
							break;
						}
					}
					if (blocked)
						continue;
					IItemHandler dest = InventoryUtil.getItemHandler(world.getTileEntity(pair.getLeft()), pair.getRight());
					int canInsert = InventoryUtil.canInsert(dest, send);
					int missing = Integer.MAX_VALUE;
					if (stockNum > 0) {
						int contains = 0;
						for (int j = 0; j < dest.getSlots(); j++) {
							if (!dest.getStackInSlot(j).isEmpty() && equal(dest.getStackInSlot(j), send)) {
								contains += dest.getStackInSlot(j).getCount();
							}
						}
						for (AbstractTransfer t : transfers) {
							if(transfers instanceof ItemTransfer) {
								ItemTransfer it = (ItemTransfer) t;
								if (t.rec.equals(pair) && equal(it.stack, send)) {
									contains += it.stack.getCount();
								}
							}
						}
						missing = stockNum - contains;
					}
					if (missing <= 0 || canInsert <= 0)
						continue;
					canInsert = Math.min(canInsert, missing);
					ItemStack x = inv.extractItem(i, canInsert, true);
					if (!x.isEmpty()) {
						ItemTransfer tr = new ItemTransfer(pos, pair.getLeft(), pair.getRight(), x);
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
						inv.extractItem(i, canInsert, false);
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
			if (!InventoryUtil.hasItemHandler(world, pa.getLeft(), pa.getRight())) {
				ite.remove();
				needSync = true;
			}
		}
		Iterator<AbstractTransfer> it = transfers.iterator();
		while (it.hasNext()) {
			AbstractTransfer t = it.next();
			if(t instanceof ItemTransfer) {
				ItemTransfer tr = (ItemTransfer)t;
				BlockPos currentPos = new BlockPos(getPos().getX() + tr.current.x, getPos().getY() + tr.current.y, getPos().getZ() + tr.current.z);
				if (tr.rec == null || !InventoryUtil.hasItemHandler(world, tr.rec.getLeft(), tr.rec.getRight()) || (!currentPos.equals(pos) && !currentPos.equals(tr.rec.getLeft()) && !world.isAirBlock(currentPos) && !throughBlocks())) {
					Block.spawnAsEntity(world, currentPos, tr.stack);
					it.remove();
					needSync = true;
					continue;
				}
				boolean received = tr.rec.getLeft().equals(currentPos);
				if (received && world.isAreaLoaded(tr.rec.getLeft(), 1)) {
					ItemStack rest = InventoryUtil.insert(world.getTileEntity(tr.rec.getLeft()), tr.stack, tr.rec.getRight());
					if (!rest.isEmpty()) {
						tr.stack = rest;
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

	public ItemStackHandler getFilter() {
		return filterHandler;
	}

	public boolean isTag() {
		return tag;
	}

	public void toggleTag() {
		this.tag = !tag;
	}

	public boolean isDurability() {
		return durability;
	}

	public void toggleDurability() {
		this.durability = !durability;
	}

	public boolean isNbt() {
		return nbt;
	}

	public void toggleNbt() {
		this.nbt = !nbt;
	}

	public boolean isWhite() {
		return white;
	}

	public void toggleWhite() {
		this.white = !white;
	}

	public boolean isMod() {
		return mod;
	}

	public void toggleMod() {
		this.mod = !mod;
	}

	public int getStockNum() {
		return stockNum;
	}

	public void incrementStockNum() {
		if(stockNum < 64) {
			this.stockNum++;
		}
	}

	public void decreaseStockNum() {
		if(stockNum > 0) {
			this.stockNum--;
		}
	}

	@Override
	public void resetOptions() {
		super.resetOptions();
		tag = false;
		durability = true;
		nbt = false;
		white = false;
		mod = false;
		stockNum = 0;
	}


	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		filterCap.invalidate();
	}
}
