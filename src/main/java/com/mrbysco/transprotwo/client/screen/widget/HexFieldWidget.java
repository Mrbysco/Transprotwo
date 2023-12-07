package com.mrbysco.transprotwo.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class HexFieldWidget extends EditBox {
	public HexFieldWidget(Font font, int x, int y, int width, int height, Component defaultValue) {
		super(font, x, y, width, height, defaultValue);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void insertText(String textToWrite) {
		if (this.isValidHexValue(textToWrite)) super.insertText(textToWrite);
	}

	@Override
	public String getValue() {
		return (this.isValidHexValue(super.getValue()) ? super.getValue() : "0");
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			this.setHighlightPos(this.getValue().length());
			this.moveCursorToEnd(false);
		}
	}

	protected boolean isValidHexValue(String value) {
		return value.matches("^[0-9A-Fa-f]+$");
	}
}
