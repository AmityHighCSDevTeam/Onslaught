package org.amityregion5.ZombieGame.client.gui;

import java.awt.geom.Rectangle2D;
import java.util.function.Supplier;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

/**
 * A class helping make a button
 * @author sergeys
 */
public class GuiRectangle extends GuiElement {

	private String		text;							// Text to draw on button
	private boolean		isEnabled	= true; //Is this button enabled
	private GlyphLayout	glyph		= new GlyphLayout(); //Glyph layout
	private Runnable 	onClick;

	public GuiRectangle(Supplier<Rectangle2D.Float> rect, String text) {
		this.text = text;
		setRectangleSupplier(rect);
	}

	public GuiRectangle(Supplier<Rectangle2D.Float> rect, String text, Runnable onClick) {
		this.onClick = onClick;
		this.text = text;
		setRectangleSupplier(rect);
	}

	/**
	 * @return the text on the button
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text on the button
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return is the button enabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled
	 *            set the button enabled or disabled
	 * @return the button
	 */
	public GuiRectangle setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		return this;
	}

	@Override
	public void draw(SpriteBatch batch, ShapeRenderer shape) {
		Color c = new Color(1, 1, 1, 1);
		// If the button is enabled
		if (isEnabled()) {
			// Get mouse location
			Vector2 touchPos = new Vector2();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY());
			// If it is inside of the button
			if (getRectangle().contains(touchPos.x, ZombieGame.instance.height - touchPos.y)) {
				// Tint the button
				c = new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f);
				
				//If mouse just released
				if (Client.mouseJustReleased() && onClick != null) {
					//Run the onClick
					onClick.run();
				}
			}
		} else {
			// If the button isn't enabled
			// Tint the button
			c = new Color(0.4f, 0.4f, 0.4f, 1f);
		}

		// If there is text for the button
		if (text != null) {
			// Get the size of the text
			glyph.setText(ZombieGame.instance.mainFont, text, c, getW(), Align.center, true);
			// Draw the text centered on the button
			ZombieGame.instance.mainFont.draw(batch, glyph, getX(), getY() + (getH() + glyph.height) / 2);
		}
		
	}
}
