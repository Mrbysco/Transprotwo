package com.mrbysco.transprotwo.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.UpdateFluidDispatcherMessage;
import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE.Mode;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.PacketDistributor;

public class FluidDispatcherScreen extends AbstractContainerScreen<FluidDispatcherContainer> {
	private final ResourceLocation TEXTURE = new ResourceLocation(Transprotwo.MOD_ID, "textures/gui/container/dispatcher.png");

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
		this.addRenderableWidget(this.mode = new Button(149 + leftPos, 41 + topPos, 20, 20, new TextComponent(Mode.getByID(container.mode[0]).toString()), (button) -> { //mode
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mode", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new TextComponent(Mode.getByID(container.mode[0]).getText()), x, y);
		}));
		this.addRenderableWidget(this.white = new Button(63 + leftPos, 16 + topPos, 20, 20, TextComponent.EMPTY, (button) -> { //whitelist
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("white", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new TextComponent(container.buttonValues[0] == 1 ? "Whitelist" : "Blacklist"), x, y);
		}));
		this.addRenderableWidget(this.mod = new Button(107 + leftPos, 16 + topPos, 20, 20, new TextComponent("MO"), (button) -> { //mod
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mod", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new TextComponent(container.buttonValues[1] == 1 ? "Check Mod ID" : "Ignore Mod ID"), x, y);
		}));
		this.addRenderableWidget(this.reset = new Button(149 + leftPos, 64 + topPos, 20, 20, new TextComponent("R"), (button) -> { //reset
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("reset", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new TextComponent("Reset"), x, y);
		}));
		dirty = true;
	}

	@Override
	public void containerTick() {
		super.containerTick();

		if (dirty) {
			mode.setMessage(new TextComponent(Mode.getByID(this.getMenu().mode[0]).toString()));
			dirty = false;
		}
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
		this.font.draw(matrixStack, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);

		FluidDispatcherContainer container = this.getMenu();
		itemRenderer.renderAndDecorateItem(new ItemStack(Items.PAPER), 2 + white.x - leftPos, 2 + white.y - topPos);
		if (!(container.buttonValues[0] == 1))
			itemRenderer.renderAndDecorateItem(new ItemStack(Blocks.BARRIER), 2 + white.x - leftPos, 2 + white.y - topPos);
//
		if (!(container.buttonValues[1] == 1))
			itemRenderer.renderAndDecorateItem(new ItemStack(Blocks.BARRIER), 2 + mod.x - leftPos, 2 + mod.y - topPos);
	}


	private void updateBlockEntity(CompoundTag compound) {
		this.dirty = true;
		PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new UpdateFluidDispatcherMessage(compound, this.getMenu().getBlockEntity().getBlockPos()));
	}
}
