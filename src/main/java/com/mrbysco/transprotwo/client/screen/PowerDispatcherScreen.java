package com.mrbysco.transprotwo.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.client.screen.widget.HexFieldWidget;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.UpdatePowerDispatcherMessage;
import com.mrbysco.transprotwo.tile.AbstractDispatcherBE.Mode;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class PowerDispatcherScreen extends AbstractContainerScreen<PowerDispatcherContainer> {
	private final ResourceLocation TEXTURE = new ResourceLocation(Transprotwo.MOD_ID, "textures/gui/container/power_dispatcher.png");

	private Button mode, reset;

	private boolean dirty;

	private final HexFieldWidget[] colorFields = new HexFieldWidget[5];

	public PowerDispatcherScreen(PowerDispatcherContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);

		this.imageHeight = 193;
	}

	@Override
	protected void init() {
		super.init();

		PowerDispatcherContainer container = this.getMenu();
		this.addRenderableWidget(this.mode = new Button(149 + leftPos, 41 + topPos, 20, 20, new TextComponent(Mode.getByID(container.mode[0]).toString()), (button) -> { //mode
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mode", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new TextComponent(Mode.getByID(container.mode[0]).getText()), x, y);
		}));
		this.addRenderableWidget(this.reset = new Button(149 + leftPos, 64 + topPos, 20, 20, new TextComponent("R"), (button) -> { //reset
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("reset", true);
			this.updateTile(tag);
		}, (button, matrix, x, y) -> {
			renderTooltip(matrix, new TextComponent("Reset"), x, y);
		}));

		for (int i = 0; i < this.colorFields.length; i++) {
			int x = 47 + leftPos;
			int y = 18 + topPos + (16 * i);
			int width = 60;
			int height = 12;
			String value = Integer.toHexString(container.lines[i]);

			this.colorFields[i] = new HexFieldWidget(this.font, x, y, width, height, new TextComponent(String.format("line %s", i + 1)));
			this.colorFields[i].setValue(value);
			this.colorFields[i].setMaxLength(6);
			this.addWidget(this.colorFields[i]);
		}

		dirty = true;
	}

	@Override
	public void containerTick() {
		super.containerTick();

		if (dirty) {
			mode.setMessage(new TextComponent(Mode.getByID(this.getMenu().mode[0]).toString()));
			dirty = false;
		}

		for (HexFieldWidget textField : this.colorFields)
			textField.tick();
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		for (HexFieldWidget textField : this.colorFields)
			textField.render(matrixStack, mouseX, mouseY, partialTicks);

		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		for(int i = 0; i < this.colorFields.length; i++) {
			HexFieldWidget field = this.colorFields[i];
			this.font.draw(matrixStack, "Color" + (i+1) + ":", field.x - (field.getWidth() / 2) - 9, field.y + 2, 4210752);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.fieldHasUpdated();
			this.minecraft.setScreen((Screen)null);
			return true;
		} else {
			boolean pressed = super.keyPressed(keyCode, scanCode, modifiers);
			this.fieldHasUpdated();
			return pressed;
		}
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		boolean typed = super.charTyped(codePoint, modifiers);
		if(typed) {
			this.fieldHasUpdated();
		}
		return typed;
	}

	@Override
	protected void insertText(String text, boolean overwrite) {
		super.insertText(text, overwrite);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (HexFieldWidget hexField : this.colorFields) {
			hexField.mouseClicked(mouseX, mouseY, button);
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
		this.font.draw(matrixStack, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);
	}

	private void fieldHasUpdated() {
		CompoundTag tag = new CompoundTag();
		for(int i = 0; i < this.colorFields.length; i++) {
			String value = this.colorFields[i].getValue();
			if(!value.isEmpty()) {
				int decimal = Integer.parseInt(value, 16);
				tag.putInt("color" + (i + 1), decimal);
			}
		}
		this.updateTile(tag);
	}

	private void updateTile(CompoundTag compound) {
		this.dirty = true;
		PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new UpdatePowerDispatcherMessage(compound, this.getMenu().getTile().getBlockPos()));
	}
}
