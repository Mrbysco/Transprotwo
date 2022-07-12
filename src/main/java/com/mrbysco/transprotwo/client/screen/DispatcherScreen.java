package com.mrbysco.transprotwo.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE.Mode;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.UpdateDispatcherMessage;
import net.minecraft.client.gui.components.Button;
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
		this.addRenderableWidget(this.mode = new Button(149 + leftPos, 41 + topPos, 20, 20, Component.literal(Mode.getByID(container.mode[0]).toString()), (button) -> { //mode
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mode", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, Component.literal(Mode.getByID(container.mode[0]).getText()), x, y);
		}));
		this.addRenderableWidget(this.tag = new Button(85 + leftPos, 16 + topPos, 20, 20, Component.empty(), (button) -> { //tag
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("tag", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, Component.literal(container.buttonValues[0] == 1 ? "Check Tag" : "Ignore Tag"), x, y);
		}));
		this.addRenderableWidget(this.durability = new Button(63 + leftPos, 16 + topPos, 20, 20, Component.literal("ME"), (button) -> { //durability
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("durability", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, Component.literal(container.buttonValues[1] == 1 ? "Check Durability" : "Ignore Durability"), x, y);
		}));
		this.addRenderableWidget(this.nbt = new Button(107 + leftPos, 16 + topPos, 20, 20, Component.literal("N"), (button) -> { //nbt
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("nbt", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, Component.literal(container.buttonValues[2] == 1 ? "Check NBT" : "Ignore NBT"), x, y);
		}));
		this.addRenderableWidget(this.white = new Button(107 + leftPos, 38 + topPos, 20, 20, Component.empty(), (button) -> { //whitelist
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("white", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, Component.literal(container.buttonValues[3] == 1 ? "Whitelist" : "Blacklist"), x, y);
		}));
		this.addRenderableWidget(this.reset = new Button(149 + leftPos, 64 + topPos, 20, 20, Component.literal("R"), (button) -> { //reset
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("reset", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, Component.literal("Reset"), x, y);
		}));
		this.addRenderableWidget(this.mod = new Button(63 + leftPos, 38 + topPos, 20, 20, Component.literal("MO"), (button) -> { //mod
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mod", true);
			this.updateBlockEntity(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, Component.literal(container.buttonValues[4] == 1 ? "Check Mod ID" : "Ignore Mod ID"), x, y);
		}));
		this.addRenderableWidget(this.minus = new Button(63 + leftPos, 63 + topPos, 14, 20, Component.literal("-"), (button) -> { //decrease
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("stockDown", true);
			this.updateBlockEntity(tag);
		}));
		this.addRenderableWidget(this.plus = new Button(113 + leftPos, 63 + topPos, 14, 20, Component.literal("+"), (button) -> { //add
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("stockUp", true);
			this.updateBlockEntity(tag);
		}));
		dirty = true;
	}

	@Override
	public void containerTick() {
		super.containerTick();

		if (dirty) {
			mode.setMessage(Component.literal(Mode.getByID(this.getMenu().mode[0]).toString()));
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

		drawString(matrixStack, font, String.valueOf(this.getMenu().stockNum[0]), leftPos + (95 - font.width("" + this.getMenu().stockNum[0]) / 2), topPos + 68, 14737632);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		this.font.draw(matrixStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
		this.font.draw(matrixStack, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);

		DispatcherContainer container = this.getMenu();
		itemRenderer.renderAndDecorateItem(new ItemStack(Blocks.GOLD_ORE), 2 + tag.x - leftPos, 2 + tag.y - topPos);
		if (!(container.buttonValues[0] == 1)) {
			itemRenderer.blitOffset += 200;
			itemRenderer.renderAndDecorateItem(new ItemStack(Blocks.BARRIER), 2 + tag.x - leftPos, 2 + tag.y - topPos);
			itemRenderer.blitOffset -= 200;
		}
		if (!(container.buttonValues[1] == 1)) //Durability
			itemRenderer.renderAndDecorateItem(new ItemStack(Blocks.BARRIER), 2 + durability.x - leftPos, 2 + durability.y - topPos);

		if (!(container.buttonValues[2] == 1))
			itemRenderer.renderAndDecorateItem(new ItemStack(Blocks.BARRIER), 2 + nbt.x - leftPos, 2 + nbt.y - topPos);

		itemRenderer.renderAndDecorateItem(new ItemStack(Items.PAPER), 2 + white.x - leftPos, 2 + white.y - topPos);
		if (!(container.buttonValues[3] == 1))
			itemRenderer.renderAndDecorateItem(new ItemStack(Blocks.BARRIER), 2 + white.x - leftPos, 2 + white.y - topPos);

		if (!(container.buttonValues[4] == 1))
			itemRenderer.renderAndDecorateItem(new ItemStack(Blocks.BARRIER), 2 + mod.x - leftPos, 2 + mod.y - topPos);

		if (isHovering(86, 65, 13, 13, mouseX, mouseY))
			renderTooltip(matrixStack, Component.literal("If greater than 0 destination inventory\n will keep that amount of items."), mouseX - leftPos, mouseY - topPos);
	}


	private void updateBlockEntity(CompoundTag compound) {
		this.dirty = true;
		PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new UpdateDispatcherMessage(compound, this.getMenu().getBlockEntity().getBlockPos()));
	}
}
