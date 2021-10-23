package com.mrbysco.transprotwo.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.UpdateDispatcherMessage;
import com.mrbysco.transprotwo.tile.AbstractDispatcherTile.Mode;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class DispatcherScreen extends ContainerScreen<DispatcherContainer> {
	private final ResourceLocation TEXTURE = new ResourceLocation(Transprotwo.MOD_ID, "textures/gui/container/dispatcher.png");

	private Button mode, tag, durability, nbt, white, reset, mod;
	private Button minus, plus;

	private boolean dirty;

	public DispatcherScreen(DispatcherContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);

		this.imageHeight = 172;
	}

	@Override
	protected void init() {
		super.init();

		DispatcherContainer container = this.getMenu();
		this.addButton(this.mode = new Button(149 + leftPos, 41 + topPos, 20, 20, new StringTextComponent(Mode.getByID(container.mode[0]).toString()), (button) -> { //mode
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("mode", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new StringTextComponent(Mode.getByID(container.mode[0]).getText()), x, y);
		}));
		this.addButton(this.tag = new Button(85 + leftPos, 16 + topPos, 20, 20, StringTextComponent.EMPTY, (button) -> { //tag
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("tag", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new StringTextComponent(container.buttonValues[0] == 1 ? "Check Tag" : "Ignore Tag"), x, y);
		}));
		this.addButton(this.durability = new Button(63 + leftPos, 16 + topPos, 20, 20, new StringTextComponent("ME"), (button) -> { //durability
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("durability", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new StringTextComponent(container.buttonValues[1] == 1 ? "Check Durability" : "Ignore Durability"), x, y);
		}));
		this.addButton(this.nbt = new Button(107 + leftPos, 16 + topPos, 20, 20, new StringTextComponent("N"), (button) -> { //nbt
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("nbt", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new StringTextComponent(container.buttonValues[2] == 1 ? "Check NBT" : "Ignore NBT"), x, y);
		}));
		this.addButton(this.white = new Button(107 + leftPos, 38 + topPos, 20, 20, StringTextComponent.EMPTY, (button) -> { //whitelist
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("white", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new StringTextComponent(container.buttonValues[3] == 1 ? "Whitelist" : "Blacklist"), x, y);
		}));
		this.addButton(this.reset = new Button(149 + leftPos, 64 + topPos, 20, 20, new StringTextComponent("R"), (button) -> { //reset
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("reset", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new StringTextComponent("Reset"), x, y);
		}));
		this.addButton(this.mod = new Button(63 + leftPos, 38 + topPos, 20, 20, new StringTextComponent("MO"), (button) -> { //mod
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("mod", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new StringTextComponent(container.buttonValues[4] == 1 ? "Check Mod ID" : "Ignore Mod ID"), x, y);
		}));
		this.addButton(this.minus = new Button(63 + leftPos, 63 + topPos, 14, 20, new StringTextComponent("-"), (button) -> { //decrease
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("stockDown", true);
			this.updateTile(tag);
		}));
		this.addButton(this.plus = new Button(113 + leftPos, 63 + topPos, 14, 20, new StringTextComponent("+"), (button) -> { //add
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("stockUp", true);
			this.updateTile(tag);
		}));
		dirty = true;
	}

	@Override
	public void tick() {
		super.tick();

		if (dirty) {
			mode.setMessage(new StringTextComponent(Mode.getByID(this.getMenu().mode[0]).toString()));
			dirty = false;
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		this.minecraft.getTextureManager().bind(TEXTURE);
		this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		drawString(matrixStack, font, String.valueOf(this.getMenu().stockNum[0]), leftPos + (95 - font.width("" + this.getMenu().stockNum[0]) / 2), topPos + 68, 14737632);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
		this.font.draw(matrixStack, this.inventory.getDisplayName(), 8, this.imageHeight - 96 + 2, 4210752);

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
			renderTooltip(matrixStack, new StringTextComponent("If greater than 0 destination inventory\n will keep that amount of items."), mouseX - leftPos, mouseY - topPos);
	}


	private void updateTile(CompoundNBT compound) {
		this.dirty = true;
		PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new UpdateDispatcherMessage(compound, this.getMenu().getTile().getBlockPos()));
	}
}
