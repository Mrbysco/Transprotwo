package com.mrbysco.transprotwo.client.screen.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

public class HexFieldWidget extends TextFieldWidget {
	public HexFieldWidget(FontRenderer font, int x, int y, int width, int height, ITextComponent defaultValue) {
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
	protected void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			this.setHighlightPos(this.getValue().length());
			this.moveCursorToEnd();
		}
	}

	protected boolean isValidHexValue(String value) {
		return value.matches("^[0-9A-Fa-f]+$");
	}
}
