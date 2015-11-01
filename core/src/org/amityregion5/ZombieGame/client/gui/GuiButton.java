package org.amityregion5.ZombieGame.client.gui;

import org.amityregion5.ZombieGame.ZombieGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

/**
 *
 * @author sergeys
 *
 */
public class GuiButton extends Sprite implements Disposable {

	private int		id;
	private String	text;				// Text to draw on button
	private boolean	isEnabled	= true;
	private GlyphLayout glyph = new GlyphLayout();

	/**
	 *
	 * @param sprite
	 *            sprite to take the texture of
	 * @param id
	 *            of the button
	 * @param text
	 *            of the button
	 * @param x
	 *            position of the button
	 * @param y
	 *            position of the button
	 * @param width
	 *            of the button
	 * @param height
	 *            of the button
	 */
	public GuiButton(Sprite sprite, int id, String text, float x, float y,
			float width, float height) {
		super(sprite);
		this.id = id;
		setX(x);
		setY(y);
		setSize(width, height);
		this.text = text;
	}

	/**
	 *
	 * @param texture
	 *            the texture of the button
	 * @param id
	 *            of the button
	 * @param text
	 *            of the button
	 * @param x
	 *            position of the button
	 * @param y
	 *            position of the button
	 * @param width
	 *            of the button
	 * @param height
	 *            of the button
	 */
	public GuiButton(Texture texture, int id, String text, float x, float y,
			float width, float height) {
		super(texture);
		this.id = id;
		setX(x);
		setY(y);
		setSize(width, height);
		this.text = text;
	}

	/**
	 *
	 * @param region
	 *            textureRegion of the button
	 * @param srcX
	 *            x on the region of the button
	 * @param srcY
	 *            y on the region of the button
	 * @param srcWidth
	 *            width on the region of the button
	 * @param srcHeight
	 *            height on the region of the button
	 * @param id
	 *            of the button
	 * @param text
	 *            of the button
	 * @param x
	 *            position of the button
	 * @param y
	 *            position of the button
	 * @param width
	 *            of the button
	 * @param height
	 *            of the button
	 */
	public GuiButton(TextureRegion region, int srcX, int srcY, int srcWidth,
			int srcHeight, int id, String text, float x, float y, float width,
			float height) {
		super(region, srcX, srcY, srcWidth, srcHeight);
		this.id = id;
		setX(x);
		setY(y);
		setSize(width, height);
		this.text = text;
	}

	/**
	 *
	 * @param x
	 *            new x position
	 * @param y
	 *            new y position
	 * @param width
	 *            new width
	 * @param height
	 *            new height
	 * @return the button
	 */
	public GuiButton setXYWH(float x, float y, float width, float height) {
		setX(x);
		setY(y);
		setSize(width, height);
		return this;
	}

	/**
	 *
	 * @return the ID number of the button
	 */
	public int getID() {
		return id;
	}

	/**
	 *
	 * @return the text on the button
	 */
	public String getText() {
		return text;
	}

	/**
	 *
	 * @param text
	 *            the text on the button
	 */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void dispose() {
	}

	/**
	 *
	 * @return is the button enabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 *
	 * @param isEnabled
	 *            set the button enabled or disabled
	 * @return the button
	 */
	public GuiButton setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		return this;
	}

	@Override
	public void draw(Batch batch) {

		setColor(new Color(1,1,1,1));
		// If the button is enabled
		if (isEnabled()) {
			// Get mouse location
			Vector2 touchPos = new Vector2();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY());
			// If it is inside of the button
			if (getBoundingRectangle().contains(touchPos.x,
					ZombieGame.instance.height - touchPos.y)) {
				// Tint the button
				setColor(new Color(27/255f, 168/255f, 55/255f, 1f));
			}
		} else {
			// If the button isn't enabled
			// Tint the button
			setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
		}

		// Draw the button
		//super.draw(batch);

		// Clear the tint

		// If there is text for the button
		if (text != null) {
			// Get the size of the text
			glyph.setText(ZombieGame.instance.mainFont, text, getColor(), getWidth(), Align.center, true);
			// Draw the text centered on the button
			ZombieGame.instance.mainFont.draw(batch, glyph, getX(),getY() + (getHeight() + glyph.height) / 2);
		}
	}
}
