package com.mrbysco.transprotwo.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.screen.widget.HexFieldWidget;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.UpdateFluidDispatcherMessage;
import com.mrbysco.transprotwo.tile.AbstractDispatcherTile.Mode;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class PowerDispatcherScreen extends ContainerScreen<PowerDispatcherContainer> {
	private final ResourceLocation TEXTURE = new ResourceLocation(Transprotwo.MOD_ID, "textures/gui/container/power_dispatcher.png");

	private Button mode, reset;

	private boolean dirty;

	private final HexFieldWidget[] colorFields = new HexFieldWidget[5];

	public PowerDispatcherScreen(PowerDispatcherContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);

		this.ySize = 172;
	}

	@Override
	protected void init() {
		super.init();

		PowerDispatcherContainer container = this.getContainer();
		this.addButton(this.mode = new Button(149 + guiLeft, 41 + guiTop, 20, 20, new StringTextComponent(Mode.getByID(container.mode[0]).toString()), (button) -> { //mode
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("mode", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new StringTextComponent(Mode.getByID(container.mode[0]).getText()), x, y);
		}));
		this.addButton(this.reset = new Button(149 + guiLeft, 64 + guiTop, 20, 20, new StringTextComponent("R"), (button) -> { //reset
			CompoundNBT tag = new CompoundNBT();
			tag.putBoolean("reset", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new StringTextComponent("Reset"), x, y);
		}));

		int offsetX = this.width - 20 - 100;
		for (int i = 0; i < this.colorFields.length; i++) {
			int x = 1 + offsetX + ((i % 3) * 35);
			int y = 1 + ((i / 3) * 22);
			int width = 28;
			int height = 17;
			String value = String.valueOf(container.lines[i]);
			value = value.substring(2);

			this.colorFields[i] = new HexFieldWidget(this.font, x, y, width, height, new StringTextComponent(String.format("line %s", i + 1)));
			this.colorFields[i].setText(value);
			this.colorFields[i].setMaxStringLength(6);
			this.addListener(this.colorFields[i]);
		}

		dirty = true;
	}

	@Override
	public void tick() {
		super.tick();

		if (dirty) {
			mode.setMessage(new StringTextComponent(Mode.getByID(this.getContainer().mode[0]).toString()));
			dirty = false;
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		this.font.drawText(matrixStack, this.title, (float)this.titleX, (float)this.titleY, 4210752);
		this.font.drawText(matrixStack, this.playerInventory.getDisplayName(), 8, this.ySize - 96 + 2, 4210752);
	}

	private void updateTile(CompoundNBT compound) {
		this.dirty = true;
		PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new UpdateFluidDispatcherMessage(compound, this.getContainer().getTile().getPos()));
	}
}
