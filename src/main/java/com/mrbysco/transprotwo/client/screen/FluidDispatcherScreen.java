package com.mrbysco.transprotwo.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE.Mode;
import com.mrbysco.transprotwo.network.message.UpdateFluidDispatcherPayload;
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
import net.neoforged.neoforge.network.PacketDistributor;

public class FluidDispatcherScreen extends AbstractContainerScreen<FluidDispatcherContainer> {
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

	private Button mode, white, reset, mod;

	private boolean dirty;

	public FluidDispatcherScreen(FluidDispatcherContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);

		this.imageHeight = 172;
	}

	@Override
	protected void init() {
		super.init();

		FluidDispatcherContainer container = this.getMenu();
		this.addRenderableWidget(this.mode = Button.builder(Component.literal(Mode.getByID(container.mode[0]).toString()), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mode", true);
			this.updateBlockEntity(tag);
		}).bounds(149 + leftPos, 41 + topPos, 20, 20).build());

		this.addRenderableWidget(this.white = Button.builder(Component.empty(), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("white", true);
			this.updateBlockEntity(tag);
		}).bounds(63 + leftPos, 16 + topPos, 20, 20).build());

		this.addRenderableWidget(this.mod = Button.builder(Component.empty(), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mod", true);
			this.updateBlockEntity(tag);
		}).bounds(107 + leftPos, 16 + topPos, 20, 20).build());

		this.addRenderableWidget(this.reset = Button.builder(Component.literal("R"), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("reset", true);
			this.updateBlockEntity(tag);
		}).bounds(149 + leftPos, 64 + topPos, 20, 20).build());
		this.reset.setTooltip(resetTooltip);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		FluidDispatcherContainer container = this.getMenu();

		Mode containerMode = Mode.getByID(container.mode[0]);
		if (!mode.getMessage().getString().equals(containerMode.toString()))
			mode.setMessage(Component.literal(containerMode.toString()));
		switch (containerMode) {
			default -> this.mode.setTooltip(nearestFirstTooltip);
			case RR -> this.mode.setTooltip(roundRobinTooltip);
			case RA -> this.mode.setTooltip(randomTooltip);
		}

		this.white.setTooltip(container.buttonValues[0] == 1 ? whitelistTooltip : blacklistTooltip);
		this.mod.setTooltip(container.buttonValues[1] == 1 ? checkModTooltip : ignoreModTooltip);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
		guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);

		FluidDispatcherContainer container = this.getMenu();
		guiGraphics.renderItem(new ItemStack(Items.PAPER), 2 + white.getX() - leftPos, 2 + white.getY() - topPos);
		if (!(container.buttonValues[0] == 1))
			guiGraphics.renderItem(new ItemStack(Blocks.BARRIER), 2 + white.getX() - leftPos, 2 + white.getY() - topPos);
		if (!(container.buttonValues[1] == 1))
			guiGraphics.renderItem(new ItemStack(Blocks.BARRIER), 2 + mod.getX() - leftPos, 2 + mod.getY() - topPos);
	}


	private void updateBlockEntity(CompoundTag compound) {
		this.dirty = true;
		PacketDistributor.SERVER.noArg().send(new UpdateFluidDispatcherPayload(compound, this.getMenu().getBlockEntity().getBlockPos()));
	}
}
