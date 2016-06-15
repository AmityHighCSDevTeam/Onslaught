package org.amityregion5.onslaught.client.gui;

import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.client.Client;

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
	private Consumer<GuiRectangle> 	onClick;
	private boolean		highlight;
	private int			alignment;

	public GuiRectangle(Supplier<Rectangle2D.Float> rect, String text) {
		this(rect, text, null);
	}

	public GuiRectangle(Supplier<Rectangle2D.Float> rect, String text, Consumer<GuiRectangle> onClick) {
		this(rect, text, onClick, Align.center, true);
	}

	public GuiRectangle(Supplier<Rectangle2D.Float> rect, String text, Consumer<GuiRectangle> onClick, int halign, boolean highlightOnMouse) {
		this.onClick = onClick;
		this.text = text;
		setRectangleSupplier(rect);
		this.highlight = highlightOnMouse;
		this.alignment = halign;
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
			if (getRectangle().contains(touchPos.x, Onslaught.instance.height - touchPos.y)) {
				// Tint the button
				if (highlight) {
					c = new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f);
				}

				//If mouse just released
				if (Client.mouseJustReleased() && onClick != null) {
					//Run the onClick
					onClick.accept(this);
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
			glyph.setText(Onslaught.instance.mainFont, text, c, getW(), alignment, true);
			// Draw the text centered on the button
			Onslaught.instance.mainFont.draw(batch, glyph, getX(), getY() + (getH() + glyph.height) / 2);
		}

	}
}
