package org.amityregion5.ZombieGame.client.window;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
import org.amityregion5.ZombieGame.client.InputAccessor;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.shop.IPurchaseable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;

/**
 * The shop window
 * @author sergeys
 *
 */
public class ShopWindow implements Screen {
	private ShapeRenderer		shapeRender			= new ShapeRenderer(); //The shape renderer
	private InGameScreen		screen; //The screen
	private PlayerModel			player; //The player
	private double				mainScrollPos, secScrollPos; //Scroll bar positions
	private GlyphLayout			glyph				= new GlyphLayout(); //The glyph layout
	private SpriteBatch			batch				= new SpriteBatch(); //The sprite batch
	private float				purchaseableHeight	= 100; //The size of a purchaseable
	private float				purchaseableBorder	= 2; //The border around a purchaseable
	private int					selected			= -1; //Which purchaseable is selected
	private float				defInfoWidth		= 400; //The info area width
	private float				infoWidth			= defInfoWidth;
	private float				secHeight			= 0; //The info area height
	private int					clickX, clickY; //The mouse posisitions
	private InputAccessor		processor; //The input processor
	private List<IPurchaseable>	cache; //A list of cached purchaseables
	private String				searchQuery			= ""; //The current search query
	private boolean				isSearchSelected	= false; //Is the search selected
	private boolean				showCursor			= false; //Is the cursor shown
	private float				timeUntilShowCursor	= 0; //The time until the cursor is shown

	public ShopWindow(InGameScreen screen, PlayerModel player) {
		this.screen = screen; //The screen
		this.player = player; //The player

		//Setup the cache
		recalculateCache();

		//Create an input processor
		processor = new InputAccessor() {
			@Override
			public boolean keyUp(int keycode) {
				//When a key is let go
				//If it is backspace, search is selected, and the query is at least 1 character in length
				if (keycode == Keys.BACKSPACE && searchQuery.length() > 0 && isSearchSelected) {
					//Remove the last character
					searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
					//Recalculate the cache
					recalculateCache();
					return true;
				}
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				//When a key is typed
				//If it is a letter, digit, or space and search is selected
				if ((Character.isLetterOrDigit(character) || character == ' ') && isSearchSelected) {
					//Add it to the end of the search query
					searchQuery += character;
					//Recalculate the cache
					recalculateCache();
					return true;
				}
				return true;
			}
			@Override
			public boolean scrolled(int amount) {
				//When the scroll wheel is used
				
				//If the mouse is in the main scroll area
				if (Gdx.input.getX() <= screen.getWidth() - 131*ZombieGame.getXScalar() - infoWidth) {
					//Scroll the main scroll area
					double maxAmt = getMaxScrollAmount() - screen.getHeight() + 200*ZombieGame.getYScalar();
					mainScrollPos = MathHelper.clamp(0, (maxAmt > 0 ? maxAmt : 0), mainScrollPos + amount * 5);
				} else {
					//If it isnt scroll the second scroll area
					double maxAmt = getSecMaxScrollAmount() - screen.getHeight() + 200*ZombieGame.getYScalar();
					secScrollPos = MathHelper.clamp(0, (maxAmt > 0 ? maxAmt : 0), secScrollPos + amount * 5);
				}
				return true;
			}
		};

		//Add the processor the the multiplexer
		Client.inputMultiplexer.addProcessor(processor);
	}

	/**
	 * Used to recalculate the cache
	 */
	private void recalculateCache() {
		//Get the current purchaseable (or null)
		IPurchaseable purch = (cache == null || selected == -1 ? null : cache.get(selected));
		
		//If there is no search query
		if (searchQuery.isEmpty()) {
			//Get the list of purchaseables and sort it by price
			cache = ZombieGame.instance.pluginManager.getPurchaseables();
			cache.sort((p1, p2) -> (int) (p1.getPrice(player) - p2.getPrice(player)));
		} else {
			//if there is a search query
			
			//Split it into sections
			String[] sections = searchQuery.split(" ");
			//Sort first by amount of times the searches appeared and then by the price
			cache = ZombieGame.instance.pluginManager.getPurchaseables().parallelStream().filter((p) -> p.numContained(sections, player) > 0)
					.sorted((p1, p2) -> {
						int countDiff = p2.numContained(sections, player) - p1.numContained(sections, player);
						if (countDiff != 0) { return countDiff; }
						double priceDiff = p1.getPrice(player) - p2.getPrice(player);
						return (int) Math.signum(priceDiff);
					}).collect(Collectors.toList());
		}
		
		//If the current purchaseable was null
		if (purch == null) {
			//Nothing is selected
			selected = -1;
		} else {
			//If it wasn't
			//Find the purchaseable and that is now the selected item
			selected = cache.indexOf(purch);
		}
	}

	@Override
	public void drawScreen(float delta, Camera camera) {

		//If it is time to show the cursor
		if (timeUntilShowCursor <= 0) {
			//Set time to 0.4 seconds
			timeUntilShowCursor = 0.4f;
			//Flip the show cursor variable
			showCursor = !showCursor;
		}
		//Tick down the time until show cursor
		timeUntilShowCursor -= delta;

		//Prepare for drawing
		drawPrepare(delta);

		//Set mouse x and y
		clickX = Gdx.input.getX();
		clickY = Gdx.input.getY();

		//Draw the main region
		drawMainRegion(delta);

		//Disable glBlend
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	/**
	 * Prepare to draw this screen
	 * 
	 * @param delta the time that has elapsed
	 */
	private void drawPrepare(float delta) {
		//Set info width
		infoWidth = defInfoWidth * ZombieGame.getXScalar();

		//Set projection matricies
		shapeRender.setProjectionMatrix(screen.getScreenProjectionMatrix());
		batch.setProjectionMatrix(screen.getScreenProjectionMatrix());
		
		//Set batch tint to none
		batch.setColor(new Color(1, 1, 1, 1));

		//Enable transparency
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

	/**
	 * Draw the main region
	 * 
	 * @param delta the time elapsed since last frame
	 */
	private void drawMainRegion(float delta) {
		//Get Coordinates for locations
		float x = 101*ZombieGame.getXScalar();
		float y = (float) (screen.getHeight() - 101*ZombieGame.getYScalar() + mainScrollPos*ZombieGame.getYScalar() - 50*ZombieGame.getYScalar());
		float w = screen.getWidth() - 232*ZombieGame.getXScalar() - infoWidth - 22*ZombieGame.getXScalar();

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

		//Clip to the Search box
		ScissorStack.pushScissors(new Rectangle(x, screen.getHeight() + (-201 + 50)*ZombieGame.getYScalar(), w, 50*ZombieGame.getYScalar()));
		{
			//Fill in the back
			shapeRender.begin(ShapeType.Filled);
			shapeRender.setColor(0.4f, 0.4f, 0.4f, 1f);
			shapeRender.rect(x + 5*ZombieGame.getXScalar(), screen.getHeight() + (-201 + 50)*ZombieGame.getYScalar(), w - 12*ZombieGame.getXScalar(), 48*ZombieGame.getYScalar());
			shapeRender.end();

			//Draw a border
			shapeRender.begin(ShapeType.Line);
			shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
			shapeRender.rect(x + 5*ZombieGame.getXScalar(), screen.getHeight() + (-201 + 50)*ZombieGame.getYScalar(), w - 12*ZombieGame.getXScalar(), 48*ZombieGame.getYScalar());
			shapeRender.end();

			//Draw the text
			batch.begin();
			if (!searchQuery.isEmpty() || isSearchSelected) {
				glyph.setText(ZombieGame.instance.mainFont, searchQuery + (showCursor && isSearchSelected ? "|" : ""), Color.WHITE, w - 24*ZombieGame.getXScalar(), Align.left, false);
			} else {
				glyph.setText(ZombieGame.instance.mainFont, "Search", Color.GRAY, w - 24*ZombieGame.getXScalar(), Align.left, false);
			}
			ZombieGame.instance.mainFont.draw(batch, glyph, x + 10*ZombieGame.getXScalar(), y + 25*ZombieGame.getYScalar() + glyph.height / 2);
			batch.end();
		}
		//Unclip the search box
		ScissorStack.popScissors();

		//Set/Unset whether the search bar is selected
		if (Gdx.input.isTouched() && Gdx.input.justTouched()) {
			if (Gdx.input.getX() >= x && Gdx.input.getX() <= x + w && screen.getHeight() - clickY >= screen.getHeight() - 151*ZombieGame.getYScalar()
					&& screen.getHeight() - clickY <= screen.getHeight() - 101*ZombieGame.getYScalar()) {
				isSearchSelected = !isSearchSelected;
			} else {
				isSearchSelected = false;
			}
		}

		//Set whether a purchaseable was clicked on
		boolean clickOnPurchaseable = false;
		if (Gdx.input.isTouched()) {
			if (clickX >= x && clickX <= x + w) {
				if (clickY >= 100*ZombieGame.getYScalar() && clickY <= screen.getHeight() - 100*ZombieGame.getYScalar()) {
					clickOnPurchaseable = true;
				}
			}
		}

		//Unselect when touched off
		if (Gdx.input.isTouched() && clickOnPurchaseable == false && clickX < x && clickX > screen.getWidth() - 232*ZombieGame.getXScalar() && clickY < 100*ZombieGame.getYScalar()
				&& clickY > screen.getHeight() - 100*ZombieGame.getYScalar()) {
			selected = -1;
		}

		//Get The weapon box size
		float trueWeaponBoxSize = (purchaseableHeight + purchaseableBorder + purchaseableBorder)*ZombieGame.getXScalar();
		//Column numbers
		int cols = ((int) ((w) / (trueWeaponBoxSize)));

		//The current mouse over index
		int mouseOverIndex = -1;

		//Get the arrow texture
		Texture upgradeArrow = TextureRegistry.getTexturesFor("upgradeArrow").get(0);

		//Clip screen to the area with the drawings
		ScissorStack.pushScissors(new Rectangle(x, 100*ZombieGame.getYScalar(), w, screen.getHeight() - 251*ZombieGame.getYScalar()));
		// Draw Weapons
		for (int i = 0; i < cache.size(); i++) {
			Gdx.gl.glEnable(GL20.GL_BLEND);

			//Get the current purchaseable
			IPurchaseable purchaseable = cache.get(i);

			//Get current row and column
			int row = i / cols;
			int col = i % cols;

			//Get the x position of the box
			float boxX = trueWeaponBoxSize * col + purchaseableBorder + x;
			//Get the y position of the box
			float boxY = y - (trueWeaponBoxSize * (row + 1));

			shapeRender.begin(ShapeType.Filled);

			//Set color to a lighter gray if the player already owns the item
			if (purchaseable.getCurrentLevel(player) >= 0) {
				shapeRender.setColor(new Color(191 / 255f, 191 / 255f, 191 / 255f, 100 / 255f));
			} else {
				shapeRender.setColor(new Color(95 / 255f, 95 / 255f, 95 / 255f, 100 / 255f));
			}
			//Draw the backrgound box
			shapeRender.rect(boxX, boxY, purchaseableHeight*ZombieGame.getXScalar(), purchaseableHeight*ZombieGame.getXScalar());

			shapeRender.end();

			//If it has an icon
			if (purchaseable.hasIcon()) {
				batch.begin();
				Texture icon = TextureRegistry.getTexturesFor(purchaseable.getIconName(player)).get(0);

				//Draw the icon
				batch.setColor(new Color(1, 1, 1, 1));
				batch.draw(icon, boxX, boxY, purchaseableHeight*ZombieGame.getXScalar(), purchaseableHeight*ZombieGame.getXScalar());

				batch.end();
			}

			//Draw a dark gray border around the box
			Gdx.gl.glLineWidth(2);
			shapeRender.begin(ShapeType.Line);

			shapeRender.setColor(Color.DARK_GRAY);
			shapeRender.rect(boxX, boxY, purchaseableHeight*ZombieGame.getXScalar(), purchaseableHeight*ZombieGame.getXScalar());

			shapeRender.end();
			Gdx.gl.glLineWidth(1);

			//If the player can purchase the item and has enough money
			if (purchaseable.canPurchase(player) && player.getMoney() >= purchaseable.getPrice(player)) {
				batch.begin();
				//Draw the upgrade arrow
				batch.draw(upgradeArrow, boxX + purchaseableHeight * 3 / 4*ZombieGame.getXScalar(), boxY, purchaseableHeight / 4*ZombieGame.getXScalar(), purchaseableHeight / 4*ZombieGame.getXScalar());
				batch.end();
			}

			//If player is moused over it set the mouse over index
			if (clickX > boxX && Gdx.graphics.getHeight() - clickY > boxY && clickX < boxX + purchaseableHeight*ZombieGame.getXScalar()
					&& Gdx.graphics.getHeight() - clickY < boxY + purchaseableHeight*ZombieGame.getXScalar()) {
				mouseOverIndex = i;
			}
		}

		//Undo the clipping
		ScissorStack.popScissors();

		//If the selected is too big
		if (selected >= cache.size()) {
			//Deselect
			selected = -1;
		}
		
		//If something is selected
		if (selected != -1) {
			//Draw the info region
			drawInfoRegion(delta);
		}

		//If something is moused over
		if (mouseOverIndex != -1) {
			//Get the thing
			IPurchaseable purchaseable = cache.get(mouseOverIndex);

			//If it is clicked on set the selected object
			if (Gdx.input.isTouched()) {
				selected = mouseOverIndex;
			}

			//Box sizes needed
			float boxWidth = 0;
			float boxHeight = 0;

			// Box Size Calculation
			//Name
			GlyphLayout nameGlyph = new GlyphLayout(ZombieGame.instance.mainFont, purchaseable.getName(), 0, purchaseable.getName().length(), Color.BLACK, 300*ZombieGame.getXScalar(),
					Align.left, false, "...");

			boxWidth = Math.max(boxWidth, nameGlyph.width + 8);
			boxHeight += nameGlyph.height + 4 + 4;

			//Description
			GlyphLayout descGlyph = new GlyphLayout(ZombieGame.instance.mainFont, purchaseable.getDescription(), Color.BLACK, 300*ZombieGame.getXScalar(), Align.left, true);

			boxWidth = Math.max(boxWidth, descGlyph.width + 8);
			boxHeight += descGlyph.height + 4;

			boxHeight += 10*ZombieGame.getYScalar();

			//Is Owned
			GlyphLayout owned = null;
			if (purchaseable.getCurrentLevel(player) > -1) {
				owned = new GlyphLayout(ZombieGame.instance.mainFont, "Owned", Color.BLUE, 300*ZombieGame.getXScalar(), Align.left, true);

				boxWidth = Math.max(boxWidth, owned.width + 8);
				boxHeight += descGlyph.height + 4;

				boxHeight += 10*ZombieGame.getYScalar();
			}

			//Can be purchased
			GlyphLayout canPurchase = null;
			if (purchaseable.canPurchase(player) && purchaseable.getPrice(player) <= player.getMoney()) {
				canPurchase = new GlyphLayout(ZombieGame.instance.mainFont, "Purchase Available", new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f), 300*ZombieGame.getXScalar(),
						Align.left, true);

				boxWidth = Math.max(boxWidth, canPurchase.width + 8);
				boxHeight += descGlyph.height + 4;

				boxHeight += 10*ZombieGame.getYScalar();
			}

			boxHeight += 10*ZombieGame.getYScalar();

			// Box Outline Drawing

			shapeRender.begin(ShapeType.Filled);

			//Draw box
			shapeRender.setColor(Color.LIGHT_GRAY);
			shapeRender.rect(clickX, screen.getHeight() - clickY - boxHeight, boxWidth, boxHeight);

			shapeRender.end();

			shapeRender.begin(ShapeType.Line);

			//Draw border
			shapeRender.setColor(Color.DARK_GRAY);
			shapeRender.rect(clickX, screen.getHeight() - clickY - boxHeight, boxWidth, boxHeight);

			shapeRender.end();

			batch.begin();

			// Text Drawing

			float textDrawing = screen.getHeight() - clickY - 3;

			//Write name
			ZombieGame.instance.mainFont.draw(batch, nameGlyph, clickX + 4, textDrawing);
			textDrawing -= nameGlyph.height + 4;

			textDrawing -= 10*ZombieGame.getYScalar();

			//Write description
			ZombieGame.instance.mainFont.draw(batch, descGlyph, clickX + 4, textDrawing);
			textDrawing -= descGlyph.height + 4;

			if (owned != null) {
				textDrawing -= 10*ZombieGame.getYScalar();

				//Write is owned
				ZombieGame.instance.mainFont.draw(batch, owned, clickX + 4, textDrawing);
				textDrawing -= owned.height + 4;
			}

			if (canPurchase != null) {
				textDrawing -= 10*ZombieGame.getYScalar();

				//Write can purchase
				ZombieGame.instance.mainFont.draw(batch, canPurchase, clickX + 4, textDrawing);
				textDrawing -= canPurchase.height + 4;
			}

			batch.end();
		}
	}

	/**
	 * Draw the info region to the right
	 * 
	 * @param delta elapsed time
	 */
	private void drawInfoRegion(float delta) {
		//Get positions
		float x = screen.getWidth() - 111*ZombieGame.getXScalar() - infoWidth;
		infoWidth -= 20*ZombieGame.getXScalar();
		float y = (float) (screen.getHeight() - 106*ZombieGame.getYScalar() + secScrollPos*ZombieGame.getYScalar());
		float startY = y;

		//Clip screen to info area
		Rectangle clipBounds = new Rectangle(x, 100*ZombieGame.getYScalar(), infoWidth, screen.getHeight() - 200*ZombieGame.getYScalar());
		ScissorStack.pushScissors(clipBounds);

		//Get selected purchaseable
		IPurchaseable selectedItem = cache.get(selected);
		batch.begin();

		//Draw the item name
		glyph.setText(ZombieGame.instance.mainFont, selectedItem.getName(), 0, selectedItem.getName().length(), new Color(1, 1, 1, 1), infoWidth - 20*ZombieGame.getXScalar(),
				Align.left, false, "...");
		ZombieGame.instance.mainFont.draw(batch, glyph, x, y);
		y -= glyph.height + 20*ZombieGame.getYScalar();

		//Draw the description (if it exists)
		if (selectedItem.getDescription() != null) {
			glyph.setText(ZombieGame.instance.mainFont, selectedItem.getDescription(), new Color(1, 1, 1, 1), infoWidth - 20*ZombieGame.getXScalar(), Align.left, true);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y);
			y -= glyph.height + 20*ZombieGame.getYScalar();
		}

		//Draw the price (if it exists)
		glyph.setText(ZombieGame.instance.mainFont, "Price: " + selectedItem.getPrice(player), new Color(1, 1, 1, 1), infoWidth - 20*ZombieGame.getXScalar(), Align.left, true);
		ZombieGame.instance.mainFont.draw(batch, glyph, x, y);
		y -= glyph.height + 20*ZombieGame.getYScalar();

		//Get level descriptors for current and next levels
		Map<String, String> currLev = selectedItem.getCurrentDescriptors(player);
		Map<String, String> nextLev = selectedItem.getNextDescriptors(player);

		//If the current level is not null
		if (currLev != null) {
			//Loop through each key
			for (String key : currLev.keySet()) {
				//Get the key
				String curr = currLev.get(key);
				//If a next value exists get it
				String next = (nextLev == null ? null : nextLev.get(key));

				//X to draw at
				float thisX = x;

				//Maximum height
				float maxH = 0;

				//Draw the key
				glyph.setText(ZombieGame.instance.mainFont, key + ": ", new Color(1, 1, 1, 1), infoWidth - 20*ZombieGame.getXScalar(), Align.left, true);
				maxH = Math.max(maxH, glyph.height);
				ZombieGame.instance.mainFont.draw(batch, glyph, thisX, y);
				thisX += glyph.width;

				//Draw the current
				glyph.setText(ZombieGame.instance.mainFont, curr + (next == null ? "" : " -> "), new Color(1, 1, 1, 1), infoWidth - 20*ZombieGame.getXScalar(), Align.left, true);
				maxH = Math.max(maxH, glyph.height);
				ZombieGame.instance.mainFont.draw(batch, glyph, thisX, y);
				thisX += glyph.width;

				//Draw the next if it exists
				if (next != null) {
					glyph.setText(ZombieGame.instance.mainFont, next, Color.GREEN, infoWidth - 20*ZombieGame.getXScalar(), Align.left, true);
					maxH = Math.max(maxH, glyph.height);
					ZombieGame.instance.mainFont.draw(batch, glyph, thisX, y);
					thisX += glyph.width;
				}

				//Update y location
				y -= maxH + 5*ZombieGame.getYScalar();
			}
		}

		//If there is a level available to this player
		if (selectedItem.hasNextLevel(player)) {
			
			//Get button positions
			float w = infoWidth - 40*ZombieGame.getXScalar();
			float h = 50*ZombieGame.getYScalar();
			x += 10*ZombieGame.getXScalar();
			y -= 60*ZombieGame.getYScalar();

			//Get price
			double price = selectedItem.getPrice(player);

			//Does the player have enough money
			boolean hasEnoughMoney = player.getMoney() >= price;

			//Clear any tint
			batch.setColor(new Color(1, 1, 1, 1));

			//Draw either upgrade or buy depending on if the player already has it
			//In either White or Dark Gray depending on if he can afford it
			glyph.setText(ZombieGame.instance.mainFont, (selectedItem.getCurrentLevel(player) > -1 ? "Upgrade" : "Buy"),
					(!hasEnoughMoney ? Color.DARK_GRAY
							: (clickX >= x && clickX <= x + w && screen.getHeight() - clickY >= y && screen.getHeight() - clickY <= y + h
							? new Color(27 / 255f, 168 / 255f, 55 / 255f, 1f) : Color.WHITE)),
					w, Align.center, true);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + h / 2 + glyph.height / 2);

			//If he pressed the button and can buy it
			if (hasEnoughMoney && Gdx.input.isTouched() && Gdx.input.justTouched()) {
				if (clickX >= x && clickX <= x + w) {
					if (screen.getHeight() - clickY >= y && screen.getHeight() - clickY <= y + h) {
						//Purchase the item
						selectedItem.onPurchase(player);
						//Subtract money
						player.setMoney(player.getMoney() - price);
						//Recalculate the cache
						recalculateCache();
					}
				}
			}

			//Update positions
			x -= 10*ZombieGame.getXScalar();
			y -= 10*ZombieGame.getYScalar();
		}

		//Finish drawing and unclip the screen
		batch.end();
		ScissorStack.popScissors();

		//Set scroll bar maximum height
		secHeight = startY - y;

		//If we took more y space that we were allowed make a scroll bar
		if (secHeight > screen.getHeight() - 200*ZombieGame.getYScalar()) {
			// Secondary Scroll bar box
			shapeRender.begin(ShapeType.Filled);
			shapeRender.setColor(0.4f, 0.4f, 0.4f, 1f);
			shapeRender.rect(screen.getWidth() - 120*ZombieGame.getXScalar(), 100*ZombieGame.getYScalar(), 20*ZombieGame.getXScalar(), screen.getHeight() - 200*ZombieGame.getYScalar());

			// Secondary Scroll Bar
			shapeRender.setColor(0.7f, 0.7f, 0.7f, 1f);
			shapeRender.rect(screen.getWidth() - 120*ZombieGame.getXScalar(), getSecScrollBarPos(), 20*ZombieGame.getXScalar(), getSecScrollBarHeight());

			shapeRender.end();
			shapeRender.begin(ShapeType.Line);

			shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
			// Scroll bar box border left line
			shapeRender.line(screen.getWidth() - 120*ZombieGame.getXScalar(), 100*ZombieGame.getYScalar(), screen.getWidth() - 120*ZombieGame.getXScalar(), screen.getHeight() - 100*ZombieGame.getYScalar());

			shapeRender.end();
		}
	}

	/**
	 * Get the maximum height of the main scroll bar
	 * @return the maximum height
	 */
	private double getMaxScrollAmount() {
		// Amount of objects * (width of single object / (max width / (width of single object + border)) + 2)
		return (purchaseableHeight / ((int) ((screen.getWidth() - 232*ZombieGame.getXScalar() - infoWidth - 22*ZombieGame.getXScalar()) / (purchaseableHeight + purchaseableBorder + purchaseableBorder))) + 2)
				* cache.size();
	}

	/**
	 * Get the maximum height of the secondary scroll bar
	 * @return the maximum height
	 */
	private double getSecMaxScrollAmount() {
		return secHeight;
	}

	/**
	 * Get the position of the main scroll bar
	 * @return the position in pixels from the top
	 */
	private float getScrollBarPos() {
		double screenHeight = screen.getHeight() - 200*ZombieGame.getYScalar();
		double pos = (screenHeight - getScrollBarHeight()) * (mainScrollPos) / (getMaxScrollAmount() - screenHeight);
		pos = (screen.getHeight() - 100*ZombieGame.getYScalar()) - pos - getScrollBarHeight();
		return (float) pos;
	}

	/**
	 * Get the position of the secondary scroll bar
	 * @return the position in pixels from the top
	 */
	private float getSecScrollBarPos() {
		double screenHeight = screen.getHeight() - 200*ZombieGame.getYScalar();
		double pos = (screenHeight - getSecScrollBarHeight()) * (secScrollPos) / (getSecMaxScrollAmount() - screenHeight);
		pos = (screen.getHeight() - 100*ZombieGame.getYScalar()) - pos - getSecScrollBarHeight();
		return (float) pos;
	}

	/**
	 * Get the height of the main scroll bar
	 * @return the height of the scroll bar
	 */
	private float getScrollBarHeight() {
		double screenHeight = screen.getHeight() - 200*ZombieGame.getYScalar();
		double height = (screenHeight * screenHeight) / getMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}

	/**
	 * Get the height of the secondary scroll bar
	 * @return the height of the scroll bar
	 */
	private float getSecScrollBarHeight() {
		double screenHeight = screen.getHeight() - 200*ZombieGame.getYScalar();
		double height = (screenHeight * screenHeight) / getSecMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}

	@Override
	public void dispose() {
		shapeRender.dispose();
		batch.dispose();
		Client.inputMultiplexer.removeProcessor(processor);
	}
}
