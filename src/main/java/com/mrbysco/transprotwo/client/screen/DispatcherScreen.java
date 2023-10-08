package com.mrbysco.transprotwo.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE.Mode;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.UpdateDispatcherMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.PacketDistributor;

public class DispatcherScreen extends AbstractContainerScreen<DispatcherContainer> {
	private final ResourceLocation TEXTURE = new ResourceLocation(Transprotwo.MOD_ID, "textures/gui/container/dispatcher.png");

	private final static Tooltip nearestFirstTooltip = Tooltip.create(Component.literal("Nearest First"));
	private final static Tooltip roundRobinTooltip = Tooltip.create(Component.literal("Round Robin"));
	private final static Tooltip randomTooltip = Tooltip.create(Component.literal("Random"));
	private final static Tooltip checkTagTooltip = Tooltip.create(Component.literal("Check Tag"));
	private final static Tooltip ignoreTagTooltip = Tooltip.create(Component.literal("Ignore Tag"));
	private final static Tooltip checkDurabilityTooltip = Tooltip.create(Component.literal("Check Durability"));
	private final static Tooltip ignoreDurabilityTooltip = Tooltip.create(Component.literal("Ignore Durability"));
	private final static Tooltip checkNBTTooltip = Tooltip.create(Component.literal("Check NBT"));
	private final static Tooltip ignoreNBTTooltip = Tooltip.create(Component.literal("Ignore NBT"));
	private final static Tooltip whitelistTooltip = Tooltip.create(Component.literal("Whitelist"));
	private final static Tooltip blacklistTooltip = Tooltip.create(Component.literal("Blacklist"));
	private final static Tooltip resetTooltip = Tooltip.create(Component.literal("Reset"));
	private final static Tooltip checkModTooltip = Tooltip.create(Component.literal("Check Mod ID"));
	private final static Tooltip ignoreModTooltip = Tooltip.create(Component.literal("Ignore Mod ID"));

	private Button mode, tag, durability, nbt, white, reset, mod;
	private Button minus, plus;

	private boolean dirty;

	public DispatcherScreen(DispatcherContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);

		this.imageHeight = 172;
	}

	@Override
	protected void init() {
		super.init();

		DispatcherContainer container = this.getMenu();
		this.addRenderableWidget(this.mode = Button.builder(Component.literal(Mode.getByID(container.mode[0]).toString()), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mode", true);
			this.updateBlockEntity(tag);
		}).bounds(149 + leftPos, 41 + topPos, 20, 20).build());

		this.addRenderableWidget(this.tag = Button.builder(Component.empty(), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("tag", true);
			this.updateBlockEntity(tag);
		}).bounds(85 + leftPos, 16 + topPos, 20, 20).build());

		this.addRenderableWidget(this.durability = Button.builder(Component.literal("ME"), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("durability", true);
			this.updateBlockEntity(tag);
		}).bounds(63 + leftPos, 16 + topPos, 20, 20).build());

		this.addRenderableWidget(this.nbt = Button.builder(Component.literal("N"), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("nbt", true);
			this.updateBlockEntity(tag);
		}).bounds(107 + leftPos, 16 + topPos, 20, 20).build());

		this.addRenderableWidget(this.white = Button.builder(Component.empty(), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("white", true);
			this.updateBlockEntity(tag);
		}).bounds(107 + leftPos, 38 + topPos, 20, 20).build());

		this.addRenderableWidget(this.reset = Button.builder(Component.literal("R"), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("reset", true);
			this.updateBlockEntity(tag);
		}).bounds(149 + leftPos, 64 + topPos, 20, 20).build());
		this.reset.setTooltip(resetTooltip);

		this.addRenderableWidget(this.mod = Button.builder(Component.empty(), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mod", true);
			this.updateBlockEntity(tag);
		}).bounds(63 + leftPos, 38 + topPos, 20, 20).build());

		this.addRenderableWidget(this.mod = Button.builder(Component.literal("-"), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("stockDown", true);
			this.updateBlockEntity(tag);
		}).bounds(63 + leftPos, 63 + topPos, 14, 20).build());

		this.addRenderableWidget(this.mod = Button.builder(Component.literal("+"), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("stockUp", true);
			this.updateBlockEntity(tag);
		}).bounds(113 + leftPos, 63 + topPos, 14, 20).build());

		dirty = true;
	}

	@Override
	public void containerTick() {
		super.containerTick();
		DispatcherContainer container = this.getMenu();

		Mode containerMode = Mode.getByID(container.mode[0]);
		if (dirty) {
			mode.setMessage(Component.literal(containerMode.toString()));
			dirty = false;
		}

		switch (containerMode) {
			default -> this.mode.setTooltip(nearestFirstTooltip);
			case RR -> this.mode.setTooltip(roundRobinTooltip);
			case RA -> this.mode.setTooltip(randomTooltip);
		}

		this.tag.setTooltip(container.buttonValues[0] == 1 ? checkTagTooltip : ignoreTagTooltip);
		this.durability.setTooltip(container.buttonValues[1] == 1 ? checkDurabilityTooltip : ignoreDurabilityTooltip);
		this.nbt.setTooltip(container.buttonValues[2] == 1 ? checkNBTTooltip : ignoreNBTTooltip);
		this.white.setTooltip(container.buttonValues[3] == 1 ? whitelistTooltip : blacklistTooltip);
		this.mod.setTooltip(container.buttonValues[4] == 1 ? checkModTooltip : ignoreModTooltip);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		guiGraphics.drawString(font, String.valueOf(this.getMenu().stockNum[0]), leftPos + (95 - font.width("" + this.getMenu().stockNum[0]) / 2), topPos + 68, 14737632, false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
		guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);

		DispatcherContainer container = this.getMenu();
		guiGraphics.renderItem(new ItemStack(Blocks.GOLD_ORE), 2 + tag.getX() - leftPos, 2 + tag.getY() - topPos);
		if (!(container.buttonValues[0] == 1)) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
			guiGraphics.renderItem(new ItemStack(Blocks.BARRIER), 2 + tag.getX() - leftPos, 2 + tag.getY() - topPos);
			guiGraphics.pose().popPose();
		}
		if (!(container.buttonValues[1] == 1)) //Durability
			guiGraphics.renderItem(new ItemStack(Blocks.BARRIER), 2 + durability.getX() - leftPos, 2 + durability.getY() - topPos);

		if (!(container.buttonValues[2] == 1))
			guiGraphics.renderItem(new ItemStack(Blocks.BARRIER), 2 + nbt.getX() - leftPos, 2 + nbt.getY() - topPos);

		guiGraphics.renderItem(new ItemStack(Items.PAPER), 2 + white.getX() - leftPos, 2 + white.getY() - topPos);
		if (!(container.buttonValues[3] == 1))
			guiGraphics.renderItem(new ItemStack(Blocks.BARRIER), 2 + white.getX() - leftPos, 2 + white.getY() - topPos);

		if (!(container.buttonValues[4] == 1))
			guiGraphics.renderItem(new ItemStack(Blocks.BARRIER), 2 + mod.getX() - leftPos, 2 + mod.getY() - topPos);

		if (isHovering(86, 65, 13, 13, mouseX, mouseY))
			guiGraphics.renderTooltip(this.font, Component.literal("If greater than 0 destination inventory\n will keep that amount of items."), mouseX - leftPos, mouseY - topPos);
	}


	private void updateBlockEntity(CompoundTag compound) {
		this.dirty = true;
		PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new UpdateDispatcherMessage(compound, this.getMenu().getBlockEntity().getBlockPos()));
	}
}
