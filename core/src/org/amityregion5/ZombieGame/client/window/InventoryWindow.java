package org.amityregion5.ZombieGame.client.window;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
import org.amityregion5.ZombieGame.client.InputAccessor;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;

/**
 * The inventory window
 * @author sergeys
 *
 */
public class InventoryWindow implements Screen {
	private ShapeRenderer	shapeRender		= new ShapeRenderer(); //The shape renderer
	private InGameScreen	screen; //The screen
	private PlayerModel		player; //The player
	private double			scrollPos; //The scroll position
	private SpriteBatch		batch			= new SpriteBatch(); //The sprite batch
	private float			weaponBoxSize	= 128; //The size of a weapon box
	private float			weaponBoxBorder	= 8; //The border between weapon boxes
	private int				mouseX, mouseY; //The mouse position
	private InputProcessor	processor; //The input processor

	public InventoryWindow(InGameScreen screen, PlayerModel player) {
		this.screen = screen;
		this.player = player;

		processor = new InputAccessor() {
			@Override
			public boolean scrolled(int amount) {
				//Scroll when scrolled
				double maxAmt = getMaxScrollAmount() - screen.getHeight() + 200*ZombieGame.getYScalar();
				scrollPos = MathHelper.clamp(0, (maxAmt > 0 ? maxAmt : 0), scrollPos + amount * 5);
				return true;
			}
		};

		Client.inputMultiplexer.addProcessor(processor);
	}

	@Override
	public void drawScreen(float delta, Camera camera) {
		//Prepare to draw
		drawPrepare(delta);

		//Set mouse position
		mouseX = Gdx.input.getX();
		mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

		//Draw
		drawMain(delta);

		//Disable blending
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	private void drawPrepare(float delta) {
		//Update matricies
		shapeRender.setProjectionMatrix(screen.getScreenProjectionMatrix());
		batch.setProjectionMatrix(screen.getScreenProjectionMatrix());

		//Enable blending/
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRender.begin(ShapeType.Filled);

		// Gray the entire screen
		shapeRender.setColor(0.5f, 0.5f, 0.5f, 0.2f);
		shapeRender.rect(0, 0, screen.getWidth(), screen.getHeight());

		// Main box in the center
		shapeRender.setColor(0.3f, 0.3f, 0.3f, 0.6f);
		shapeRender.rect(100*ZombieGame.getXScalar(), 100*ZombieGame.getYScalar(), screen.getWidth() - 200*ZombieGame.getXScalar(), screen.getHeight() - 200*ZombieGame.getYScalar());

		shapeRender.end();

		shapeRender.begin(ShapeType.Line);

		shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
		// Main box border
		shapeRender.rect(100*ZombieGame.getXScalar(), 100*ZombieGame.getYScalar(), screen.getWidth() - 200*ZombieGame.getXScalar(), screen.getHeight() - 200*ZombieGame.getYScalar());

		shapeRender.end();
	}

	private void drawMain(float delta) {
		//Calculate drawing size and points
		float x = 101*ZombieGame.getXScalar();
		float y = (float) (screen.getHeight() - 101*ZombieGame.getYScalar() + scrollPos);
		float w = screen.getWidth() - 221*ZombieGame.getXScalar();

		shapeRender.begin(ShapeType.Filled);
		// Main Scroll bar box
		shapeRender.setColor(0.4f, 0.4f, 0.4f, 1f);
		shapeRender.rect(x + w, 100*ZombieGame.getYScalar(), 20*ZombieGame.getXScalar(), screen.getHeight() - 200*ZombieGame.getYScalar());

		// Main Scroll Bar
		shapeRender.setColor(0.7f, 0.7f, 0.7f, 1f);
		shapeRender.rect(x + w, getScrollBarPos(), 20*ZombieGame.getXScalar(), getScrollBarHeight());
		shapeRender.end();

		shapeRender.begin(ShapeType.Line);

		shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
		// Scroll bar box border left line
		shapeRender.line(x + w, 100*ZombieGame.getYScalar(), x + w, screen.getHeight() - 100*ZombieGame.getYScalar());

		shapeRender.end();

		//Get The weapon box size
		float trueWeaponBoxSize = (weaponBoxSize + weaponBoxBorder + weaponBoxBorder)*ZombieGame.getXScalar();
		//Column numbers
		int cols = ((int) ((w) / (trueWeaponBoxSize)));

		int mouseOverIndex = -1;

		//Clip the screen
		Rectangle clipBounds = new Rectangle(x, 100 * ZombieGame.getYScalar(), w, screen.getHeight() - 201 * ZombieGame.getYScalar());
		ScissorStack.pushScissors(clipBounds);
		for (int i = 0; i < player.getWeapons().size(); i++) {
			//Enable blending
			Gdx.gl.glEnable(GL20.GL_BLEND);

			//Get row and column
			int row = i / cols;
			int col = i % cols;
			
			//Get coordinates of the box
			float boxX = trueWeaponBoxSize * col + weaponBoxBorder + x;
			float boxY = y - (trueWeaponBoxSize * (row + 1));

			shapeRender.begin(ShapeType.Filled);

			//Draw the inside box
			shapeRender.setColor(new Color(191 / 255f, 191 / 255f, 191 / 255f, 100 / 255f));
			shapeRender.rect(boxX, boxY, weaponBoxSize*ZombieGame.getXScalar(), weaponBoxSize*ZombieGame.getXScalar());

			shapeRender.end();

			//Get the weapon
			WeaponStack weapon = player.getWeapons().get(i);

			batch.begin();
			Sprite s = TextureRegistry.getAtlas().createSprite(TextureRegistry.getTextureNamesFor(weapon.getIconTextureName()).get(0));

			//Draw the icon
			batch.setColor(new Color(1, 1, 1, 1));
			s.setBounds(boxX, boxY, weaponBoxSize*ZombieGame.getXScalar(), weaponBoxSize*ZombieGame.getXScalar());
			s.draw(batch);

			batch.end();

			Gdx.gl.glLineWidth(2);
			shapeRender.begin(ShapeType.Line);

			//Draw the outline
			shapeRender.setColor(Color.DARK_GRAY);
			shapeRender.rect(boxX, boxY, weaponBoxSize*ZombieGame.getXScalar(), weaponBoxSize*ZombieGame.getXScalar());

			shapeRender.end();
			Gdx.gl.glLineWidth(1);

			//If moused over set mouseover
			if (mouseX > boxX && mouseY > boxY && mouseX < boxX + weaponBoxSize*ZombieGame.getXScalar() && mouseY < boxY + weaponBoxSize*ZombieGame.getXScalar()) {
				mouseOverIndex = i;
			}
		}
		//Unclip the screen
		ScissorStack.popScissors();

		//If a weapon is moused over
		if (mouseOverIndex != -1) {
			//Get the weapon
			WeaponStack weapon = player.getWeapons().get(mouseOverIndex);

			//If the mouse is pressed
			if (Gdx.input.isTouched()) {
				//Set the current hotbar slot to the weapon and clear other weapons that appear in the hotbar of the same kind
				for (int i = 0; i < player.getHotbar().length; i++) {
					if (i == player.getCurrWeapIndex()) {
						player.getHotbar()[i] = weapon;
					} else if (player.getHotbar()[i] == weapon) {
						player.getHotbar()[i] = new WeaponStack(new NullWeapon());
					}
				}
			}

			//Size of the box
			float boxWidth = 0;
			float boxHeight = 0;

			//Calculate name
			GlyphLayout nameGlyph = new GlyphLayout(ZombieGame.instance.mainFont, weapon.getWeapon().getName(), 0, weapon.getWeapon().getName().length(),
					Color.BLACK, 300*ZombieGame.getXScalar(), Align.left, false, "...");

			boxWidth = Math.max(boxWidth, nameGlyph.width + 8);
			boxHeight += nameGlyph.height + 8*ZombieGame.getYScalar();

			//Calculate description
			GlyphLayout descGlyph = new GlyphLayout(ZombieGame.instance.mainFont, weapon.getWeapon().getDescription(), Color.BLACK, 300*ZombieGame.getXScalar(), Align.left, true);

			boxWidth = Math.max(boxWidth, descGlyph.width + 8);
			boxHeight += descGlyph.height + 4*ZombieGame.getYScalar();

			boxHeight += 10*ZombieGame.getYScalar();

			
			
			shapeRender.begin(ShapeType.Filled);

			//Draw the box
			shapeRender.setColor(Color.LIGHT_GRAY);
			shapeRender.rect(mouseX, mouseY - boxHeight, boxWidth, boxHeight);

			shapeRender.end();

			shapeRender.begin(ShapeType.Line);

			//Draw the outline
			shapeRender.setColor(Color.DARK_GRAY);
			shapeRender.rect(mouseX, mouseY - boxHeight, boxWidth, boxHeight);

			shapeRender.end();

			batch.begin();

			
			//Draw the text
			float textDrawing = mouseY - 3;

			//Name
			ZombieGame.instance.mainFont.draw(batch, nameGlyph, mouseX + 4, textDrawing);
			textDrawing -= nameGlyph.height + 4;

			textDrawing -= 10;

			//Description
			ZombieGame.instance.mainFont.draw(batch, descGlyph, mouseX + 4, textDrawing);
			textDrawing -= descGlyph.height + 4;

			batch.end();
		} else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			//If right mouse click
			//Clear current slot
			player.getHotbar()[player.getCurrWeapIndex()] = new WeaponStack(new NullWeapon());
		}
	}

	@Override
	public void dispose() {
		batch.dispose(); //Remove input processor
		Client.inputMultiplexer.removeProcessor(processor);
	}

	/**
	 * Calculate the maximum amount you can scroll
	 * @return the maximum scroll amount
	 */
	private double getMaxScrollAmount() {
		return (weaponBoxSize / ((int) ((screen.getWidth() - 221*ZombieGame.getXScalar()) / (weaponBoxSize + weaponBoxBorder + weaponBoxBorder))) + 2)
				* player.getWeapons().size();
	}

	/**
	 * Get the scroll bar position
	 * @return the scroll bar position
	 */
	private float getScrollBarPos() {
		double screenHeight = screen.getHeight() - 200*ZombieGame.getYScalar();
		double pos = (screenHeight - getScrollBarHeight()) * (scrollPos) / (getMaxScrollAmount() - screenHeight);
		pos = (screen.getHeight() - 100*ZombieGame.getYScalar()) - pos - getScrollBarHeight();
		return (float) pos;
	}

	/**
	 * Get the height of the scroll bar
	 * @return the height of the scroll bar
	 */
	private float getScrollBarHeight() {
		double screenHeight = screen.getHeight() - 200*ZombieGame.getYScalar();
		double height = (screenHeight * screenHeight) / getMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}
}
