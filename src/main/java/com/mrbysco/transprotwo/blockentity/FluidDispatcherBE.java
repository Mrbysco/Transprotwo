package com.mrbysco.transprotwo.blockentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.blockentity.transfer.AbstractTransfer;
import com.mrbysco.transprotwo.blockentity.transfer.FluidTransfer;
import com.mrbysco.transprotwo.client.screen.FluidDispatcherContainer;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.TransferParticlePayload;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import com.mrbysco.transprotwo.util.DistanceHelper;
import com.mrbysco.transprotwo.util.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FluidDispatcherBE extends AbstractDispatcherBE {
	private boolean white = false;
	private boolean mod = false;

	public final ItemStackHandler filterHandler = new ItemStackHandler(9) {
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
		}
	};

	public FluidDispatcherBE(BlockPos pos, BlockState state) {
		super(TransprotwoRegistry.FLUID_DISPATCHER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(Transprotwo.MOD_ID + ".container.fluid_dispatcher");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player playerEntity) {
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
		IFluidHandlerItem fluidHandlerItem = filterItem.getCapability(Capabilities.FluidHandler.ITEM);
		if (fluidHandlerItem != null) {
			for (int i = 0; i < fluidHandlerItem.getTanks(); i++) {
				FluidStack checkStack = fluidHandlerItem.getFluidInTank(i);

				if (checkStack.isEmpty() || fluid.isEmpty())
					return false;
				if (mod && BuiltInRegistries.FLUID.getKey(checkStack.getFluid()).getNamespace().equals(BuiltInRegistries.FLUID.getKey(fluid.getFluid()).getNamespace()))
					return true;
				if (checkStack.isFluidEqual(fluid)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void load(CompoundTag compound) {
		ListTag transferList = compound.getList("transfers", 10);
		this.transfers = Sets.newHashSet();
		for (int i = 0; i < transferList.size(); i++)
			this.transfers.add(FluidTransfer.loadFromNBT(transferList.getCompound(i)));

		this.filterHandler.deserializeNBT(compound.getCompound("filter"));

		white = compound.getBoolean("white");
		mod = compound.getBoolean("mod");
		super.load(compound);
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.put("filter", filterHandler.serializeNBT());

		compound.putBoolean("white", white);
		compound.putBoolean("mod", mod);
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
			IFluidHandler originHandler = getOriginHandler();
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

					IFluidHandler dest = FluidHelper.getFluidHandler(level, pair.getLeft(), pair.getRight());
					int canInsert = FluidHelper.canInsert(dest, send);
					if (canInsert <= 0)
						continue;

					FluidStack x = originHandler.drain(send, IFluidHandler.FluidAction.SIMULATE);
					if (!x.isEmpty()) {
						FluidTransfer tr = new FluidTransfer(worldPosition, pair.getLeft(), pair.getRight(), x);
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
						originHandler.drain(send, IFluidHandler.FluidAction.EXECUTE);
						return true;
					}
				}
		}
		return false;
	}

	@Override
	public void summonParticles(CompoundTag nbt) {
		PacketHandler.sendToNearbyPlayers(new TransferParticlePayload(nbt), getBlockPos(), 32, this.getLevel().dimension());
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, FluidDispatcherBE fluidDispatcher) {
		fluidDispatcher.moveItems();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, FluidDispatcherBE fluidDispatcher) {
		fluidDispatcher.moveItems();
		boolean needSync = false;
		Iterator<Pair<BlockPos, Direction>> ite = fluidDispatcher.targets.iterator();
		while (ite.hasNext()) {
			Pair<BlockPos, Direction> pa = ite.next();
			if (!FluidHelper.hasFluidHandler(level, pa.getLeft(), pa.getRight())) {
				ite.remove();
				needSync = true;
			}
		}

		IFluidHandler originHandler = fluidDispatcher.getOriginHandler();
		if (originHandler == null)
			return;

		Iterator<AbstractTransfer> it = fluidDispatcher.transfers.iterator();
		while (it.hasNext()) {
			AbstractTransfer t = it.next();
			if (t instanceof FluidTransfer tr) {
				BlockPos currentPos = BlockPos.containing(pos.getX() + tr.current.x, pos.getY() + tr.current.y, pos.getZ() + tr.current.z);
				if (tr.rec == null || !FluidHelper.hasFluidHandler(level, tr.rec.getLeft(), tr.rec.getRight()) ||
						(!currentPos.equals(pos) && !currentPos.equals(tr.rec.getLeft()) && !level.isEmptyBlock(currentPos) && !fluidDispatcher.throughBlocks())) {
					it.remove();
					needSync = true;
					continue;
				}
				boolean received = tr.rec.getLeft().equals(currentPos);
				if (received && level.isAreaLoaded(tr.rec.getLeft(), 1)) {
					FluidStack rest = FluidHelper.insert(level, tr.rec.getLeft(), tr.fluidStack, tr.rec.getRight());
					if (!rest.isEmpty()) {
						tr.fluidStack = rest;
						for (AbstractTransfer at : fluidDispatcher.transfers) {
							if (at.rec.equals(tr.rec)) {
								if (!at.blocked)
									needSync = true;
								at.blocked = true;
							}
						}
					} else {
						for (AbstractTransfer at : fluidDispatcher.transfers) {
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
		boolean started = fluidDispatcher.startTransfer();
		if (needSync || started)
			fluidDispatcher.refreshClient();
	}

	public IFluidHandler getOriginHandler() {
		Direction face = level.getBlockState(worldPosition).getValue(DirectionalBlock.FACING);
		if (!level.isAreaLoaded(worldPosition.relative(face), 1) && level.getBlockEntity(worldPosition.relative(face)) == null)
			return null;
		return FluidHelper.getFluidHandler(level, worldPosition.relative(face), face.getOpposite());
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
}
