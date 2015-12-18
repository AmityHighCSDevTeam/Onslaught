package org.amityregion5.ZombieGame.client.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

/**
 * A class representing input data
 * @author sergeys
 *
 */
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

	/**
	 * Set this input data to keyboard of int value
	 * 
	 * @param keyboard the key value
	 */
	public void setKeyboard(int keyboard) {
		this.keyboard = keyboard;
		mouseButton = -1;
	}

	/**
	 * Set this input data to mouse of int value
	 * @param mouseButton the mouse button
	 */
	public void setMouseButton(int mouseButton) {
		this.mouseButton = mouseButton;
		keyboard = -1;
	}

	/**
	 * Get the keyboard data
	 * 
	 * @return the key or -1
	 */
	public int getKeyboard() {
		return keyboard;
	}

	/**
	 * Get the mouse data
	 * 
	 * @return the mouse button or -1
	 */
	public int getMouseButton() {
		return mouseButton;
	}

	/**
	 * Is this input down right now
	 * 
	 * @return a boolean representing whether this input is pressed right now
	 */
	public boolean isDown() {
		if (isKeyboard()) {
			return isKeyDown();
		} else if (isMouseButton()) { return isMouseDown(); }
		return false;
	}

	/**
	 * Has this input just gone down
	 * Warning: May not be accurate for mouse inputs
	 * 
	 * @return has this input just gone down
	 */
	public boolean isJustDown() {
		if (isKeyboard()) {
			return isKeyJustDown();
		} else if (isMouseButton()) { return isMouseJustDown(); }
		return false;
	}

	/**
	 * Is the key down
	 * 
	 * @return is the key down
	 */
	public boolean isKeyDown() {
		return Gdx.input.isKeyPressed(keyboard);
	}

	/**
	 * Is the mouse button down
	 * 
	 * @return is the mouse button down
	 */
	public boolean isMouseDown() {
		return Gdx.input.isButtonPressed(mouseButton);
	}

	/**
	 * Did the key just go down
	 * 
	 * @return has the key just been pressed
	 */
	public boolean isKeyJustDown() {
		return Gdx.input.isKeyJustPressed(keyboard);
	}

	/**
	 * Warning: May not be accurate
	 *
	 * @return has the mouse just been pressed
	 */
	public boolean isMouseJustDown() {
		return Gdx.input.justTouched() && Gdx.input.isButtonPressed(mouseButton);
	}

	/**
	 * Is this a keyboard type input data
	 * 
	 * @return is this a keyboard type
	 */
	public boolean isKeyboard() {
		return keyboard != -1 && mouseButton == -1;
	}

	/**
	 * Is this a mouse type input data
	 * 
	 * @return is this a mouse type
	 */
	public boolean isMouseButton() {
		return keyboard == -1 && mouseButton != -1;
	}

	/**
	 * Get the name of this input data
	 * 
	 * @return the name of the input data
	 */
	public String getName() {
		//If a keyboard
		if (isKeyboard()) {
			//Get the key name
			return Keys.toString(keyboard);
		} else if (isMouseButton()) {
			//If mouse
			//Depends on mouse button
			if (mouseButton == Buttons.LEFT) {
				return "Left Mouse";
			} else if (mouseButton == Buttons.RIGHT) {
				return "Right Mouse";
			} else if (mouseButton == Buttons.MIDDLE) { return "Middle Mouse"; }
		}
		//If neither it is an error
		return "ERROR";
	}

	@Override
	public boolean equals(Object obj) {
		//is it also an input data
		if (obj instanceof InputData) {
			InputData data = (InputData) obj;
			//Is it the same mouse button and keyboard key
			if (data.getKeyboard() == getKeyboard() && data.getMouseButton() == getMouseButton()) { return true; }
			return false;
		}
		return false;
	}
}
