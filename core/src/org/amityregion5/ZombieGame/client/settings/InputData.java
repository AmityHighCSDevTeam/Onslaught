package org.amityregion5.ZombieGame.client.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

public class InputData {
	private int	keyboard	= -1;
	private int	mouseButton	= -1;

	public InputData() {}

	/**
	 * @param keyboard
	 *            if true then it is a keyboard type
	 * @param value
	 */
	public InputData(boolean keyboard, int value) {
		if (keyboard) {
			setKeyboard(value);
		} else {
			setMouseButton(value);
		}
	}

	public void setKeyboard(int keyboard) {
		this.keyboard = keyboard;
		mouseButton = -1;
	}

	public void setMouseButton(int mouseButton) {
		this.mouseButton = mouseButton;
		keyboard = -1;
	}

	public int getKeyboard() {
		return keyboard;
	}

	public int getMouseButton() {
		return mouseButton;
	}

	public boolean isDown() {
		if (isKeyboard()) {
			return isKeyDown();
		} else if (isMouseButton()) { return isMouseDown(); }
		return false;
	}

	public boolean isJustDown() {
		if (isKeyboard()) {
			return isKeyJustDown();
		} else if (isMouseButton()) { return isMouseJustDown(); }
		return false;
	}

	public boolean isKeyDown() {
		return Gdx.input.isKeyPressed(keyboard);
	}

	public boolean isMouseDown() {
		return Gdx.input.isButtonPressed(mouseButton);
	}

	public boolean isKeyJustDown() {
		return Gdx.input.isKeyJustPressed(keyboard);
	}

	/**
	 * Warning: May not be accurate
	 *
	 * @return
	 */
	public boolean isMouseJustDown() {
		return Gdx.input.justTouched() && Gdx.input.isButtonPressed(mouseButton);
	}

	public boolean isKeyboard() {
		return keyboard != -1 && mouseButton == -1;
	}

	public boolean isMouseButton() {
		return keyboard == -1 && mouseButton != -1;
	}

	public String getName() {
		if (isKeyboard()) {
			return Keys.toString(keyboard);
		} else if (isMouseButton()) {
			if (mouseButton == Buttons.LEFT) {
				return "Left Mouse";
			} else if (mouseButton == Buttons.RIGHT) {
				return "Right Mouse";
			} else if (mouseButton == Buttons.MIDDLE) { return "Middle Mouse"; }
		}
		return "ERROR";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InputData) {
			InputData data = (InputData) obj;
			if (data.getKeyboard() == getKeyboard() && data.getMouseButton() == getMouseButton()) { return true; }
			return false;
		}
		return false;
	}
}
