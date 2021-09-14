package com.mrbysco.transprotwo.tile;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.screen.FluidDispatcherContainer;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.TransferParticleMessage;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.tile.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.tile.transfer.FluidTransfer;
import com.mrbysco.transprotwo.util.DistanceHelper;
import com.mrbysco.transprotwo.util.FluidHelper;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FluidDispatcherTile extends AbstractDispatcherTile {
	private boolean white = false;
	private boolean mod = false;

	public final ItemStackHandler filterHandler = new ItemStackHandler(9) {
		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
		}
	};
	private LazyOptional<IItemHandler> filterCap = LazyOptional.of(() -> filterHandler);

	public FluidDispatcherTile() {
		super(TransprotwoRegistry.FLUID_DISPATCHER_TILE.get());
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(Transprotwo.MOD_ID + ".container.fluid_dispatcher");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new FluidDispatcherContainer(id, playerInv, this);
	}

	public boolean canTransfer(FluidStack stack) {
		if (stack.isEmpty())
			return false;
		for (int i = 0; i < filterHandler.getSlots(); i++) {
			ItemStack s = filterHandler.getStackInSlot(i);
			if (!s.isEmpty() && matchesFluidFilter(stack, s))
				return white;
		}
		return !white;
	}

	public boolean matchesFluidFilter(FluidStack fluid, ItemStack filterItem) {
		IFluidHandlerItem fluidHandlerItem = filterItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
		if(fluidHandlerItem != null) {
			for(int i = 0; i < fluidHandlerItem.getTanks(); i++) {
				FluidStack checkStack = fluidHandlerItem.getFluidInTank(i);

				if (checkStack.isEmpty() || fluid.isEmpty())
					return false;
				if (mod && checkStack.getFluid().getRegistryName().getNamespace().equals(fluid.getFluid().getRegistryName().getNamespace()))
					return true;
				if(checkStack.isFluidEqual(fluid)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		ListNBT transferList = compound.getList("transfers", 10);
		this.transfers = Sets.newHashSet();
		for (int i = 0; i < transferList.size(); i++)
			this.transfers.add(FluidTransfer.loadFromNBT(transferList.getCompound(i)));

		this.filterHandler.deserializeNBT(compound.getCompound("filter"));

		white = compound.getBoolean("white");
		mod = compound.getBoolean("mod");
		super.read(state, compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("filter", filterHandler.serializeNBT());

		compound.putBoolean("white", white);
		compound.putBoolean("mod", mod);
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
			IFluidHandler originHandler = getOriginHandler();
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
			for (Pair<BlockPos, Direction> pair : lis)
				for (int i = 0; i < originHandler.getTanks(); i++) {
					if (originHandler.getFluidInTank(i).isEmpty() || !canTransfer(originHandler.getFluidInTank(i)))
						continue;
					int max = getStackSize() * 1000;
					FluidStack send = getExtractStack(originHandler.getFluidInTank(i), max);
					boolean blocked = false;
					for (AbstractTransfer t : transfers) {
						if (t.rec.equals(pair) && t.blocked) {
							blocked = true;
							break;
						}
					}
					if (blocked)
						continue;

					IFluidHandler dest = FluidHelper.getFluidHandler(world.getTileEntity(pair.getLeft()), pair.getRight());
					int canInsert = FluidHelper.canInsert(dest, send);
					if (canInsert <= 0)
						continue;

					FluidStack x = originHandler.drain(send, FluidAction.SIMULATE);
					if (!x.isEmpty()) {
						FluidTransfer tr = new FluidTransfer(pos, pair.getLeft(), pair.getRight(), x);
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
						originHandler.drain(send, FluidAction.EXECUTE);
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
			if (!FluidHelper.hasFluidHandler(world, pa.getLeft(), pa.getRight())) {
				ite.remove();
				needSync = true;
			}
		}

		IFluidHandler originHandler = getOriginHandler();
		if (originHandler == null)
			return;

		Iterator<AbstractTransfer> it = transfers.iterator();
		while (it.hasNext()) {
			AbstractTransfer t = it.next();
			if(t instanceof FluidTransfer) {
				FluidTransfer tr = (FluidTransfer)t;
				BlockPos currentPos = new BlockPos(getPos().getX() + tr.current.x, getPos().getY() + tr.current.y, getPos().getZ() + tr.current.z);
				if (tr.rec == null || !FluidHelper.hasFluidHandler(world, tr.rec.getLeft(), tr.rec.getRight()) || (!currentPos.equals(pos) && !currentPos.equals(tr.rec.getLeft()) && !world.isAirBlock(currentPos) && !throughBlocks())) {
					it.remove();
					needSync = true;
					continue;
				}
				boolean received = tr.rec.getLeft().equals(currentPos);
				if (received && world.isAreaLoaded(tr.rec.getLeft(), 1)) {
					FluidStack rest = FluidHelper.insert(world.getTileEntity(tr.rec.getLeft()), tr.fluidStack, tr.rec.getRight());
					if (!rest.isEmpty()) {
						tr.fluidStack = rest;
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

	public IFluidHandler getOriginHandler() {
		Direction face = world.getBlockState(pos).get(DirectionalBlock.FACING);
		if (!world.isAreaLoaded(pos.offset(face), 1) && world.getTileEntity(pos.offset(face)) == null)
			return null;
		return FluidHelper.getFluidHandler(world.getTileEntity(pos.offset(face)), face.getOpposite());
	}

	public FluidStack getExtractStack(FluidStack currentFluid, int max) {
		return new FluidStack(currentFluid.getFluid(), max);
	}

	public ItemStackHandler getFilter() {
		return filterHandler;
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

	@Override
	public void resetOptions() {
		super.resetOptions();
		white = false;
		mod = false;
	}

	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		filterCap.invalidate();
	}
}
