package com.mrbysco.transprotwo.client.screen.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

public class HexFieldWidget extends TextFieldWidget {
	public HexFieldWidget(FontRenderer font, int x, int y, int width, int height, ITextComponent defaultValue) {
		super(font, x, y, width, height, defaultValue);
	}
	//

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void writeText(String textToWrite) {
		if (this.isValidHexValue(textToWrite)) super.writeText(textToWrite);
	}

	@Override
	public String getText() {
		return (this.isValidHexValue(super.getText()) ? super.getText() : "0");
	}

	@Override
	protected void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			this.setSelectionPos(this.getText().length());
			this.setCursorPositionEnd();
		}
	}

	protected boolean isValidHexValue(String value) {
		return value.matches("0[xX][0-9a-fA-F]+");
	}
}
