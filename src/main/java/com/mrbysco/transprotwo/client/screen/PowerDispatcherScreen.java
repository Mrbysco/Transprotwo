package com.mrbysco.transprotwo.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.blockentity.AbstractDispatcherBE.Mode;
import com.mrbysco.transprotwo.client.screen.widget.HexFieldWidget;
import com.mrbysco.transprotwo.network.PacketHandler;
import com.mrbysco.transprotwo.network.message.UpdatePowerDispatcherMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

public class PowerDispatcherScreen extends AbstractContainerScreen<PowerDispatcherContainer> {
	private final ResourceLocation TEXTURE = new ResourceLocation(Transprotwo.MOD_ID, "textures/gui/container/power_dispatcher.png");

	private final static Tooltip nearestFirstTooltip = Tooltip.create(Component.literal("Nearest First"));
	private final static Tooltip roundRobinTooltip = Tooltip.create(Component.literal("Round Robin"));
	private final static Tooltip randomTooltip = Tooltip.create(Component.literal("Random"));
	private final static Tooltip resetTooltip = Tooltip.create(Component.literal("Reset"));

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
		this.addRenderableWidget(this.mode = Button.builder(Component.literal(Mode.getByID(container.mode[0]).toString()), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("mode", true);
			this.updateBlockEntity(tag);
		}).bounds(149 + leftPos, 41 + topPos, 20, 20).build());


		this.addRenderableWidget(this.reset = Button.builder(Component.literal("R"), (button) -> {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("reset", true);
			this.updateBlockEntity(tag);
		}).bounds(149 + leftPos, 64 + topPos, 20, 20).build());
		this.reset.setTooltip(resetTooltip);

		for (int i = 0; i < this.colorFields.length; i++) {
			int x = 47 + leftPos;
			int y = 18 + topPos + (16 * i);
			int width = 60;
			int height = 12;
			String value = Integer.toHexString(container.lines[i]);

			this.colorFields[i] = new HexFieldWidget(this.font, x, y, width, height, Component.literal(String.format("line %s", i + 1)));
			this.colorFields[i].setValue(value);
			this.colorFields[i].setMaxLength(6);
			this.addWidget(this.colorFields[i]);
		}
	}

	@Override
	public void containerTick() {
		super.containerTick();
		PowerDispatcherContainer container = this.getMenu();

		Mode containerMode = Mode.getByID(container.mode[0]);
		if (!mode.getMessage().getString().equals(containerMode.toString()))
			mode.setMessage(Component.literal(containerMode.toString()));
		switch (containerMode) {
			default -> this.mode.setTooltip(nearestFirstTooltip);
			case RR -> this.mode.setTooltip(roundRobinTooltip);
			case RA -> this.mode.setTooltip(randomTooltip);
		}

		for (HexFieldWidget textField : this.colorFields)
			textField.tick();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		for (HexFieldWidget textField : this.colorFields)
			textField.render(guiGraphics, mouseX, mouseY, partialTicks);

		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		for (int i = 0; i < this.colorFields.length; i++) {
			HexFieldWidget field = this.colorFields[i];
			guiGraphics.drawString(this.font, "Color" + (i + 1) + ":", field.getX() - (field.getWidth() / 2) - 9, field.getY() + 2, 4210752, false);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.fieldHasUpdated();
			this.minecraft.setScreen((Screen) null);
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
		if (typed) {
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
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
		guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
	}

	private void fieldHasUpdated() {
		CompoundTag tag = new CompoundTag();
		for (int i = 0; i < this.colorFields.length; i++) {
			String value = this.colorFields[i].getValue();
			if (!value.isEmpty()) {
				int decimal = Integer.parseInt(value, 16);
				tag.putInt("color" + (i + 1), decimal);
			}
		}
		this.updateBlockEntity(tag);
	}

	private void updateBlockEntity(CompoundTag compound) {
		this.dirty = true;
		PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new UpdatePowerDispatcherMessage(compound, this.getMenu().getBlockEntity().getBlockPos()));
	}
}
