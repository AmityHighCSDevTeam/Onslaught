package org.amityregion5.ZombieGame.client.window;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.Client;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.shop.IPurchaseable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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

public class ShopWindow implements Screen {
	private ShapeRenderer shapeRender = new ShapeRenderer();
	private InGameScreen screen;
	private PlayerModel player;
	private double mainScrollPos, secScrollPos;
	private GlyphLayout glyph = new GlyphLayout();
	private SpriteBatch batch = new SpriteBatch();
	private float purchaseableHeight = 100;
	private float purchaseableBorder = 2;
	private int selected = -1;
	private float defInfoWidth = 400;
	private float infoWidth = defInfoWidth;
	private float secHeight = 0;
	private int clickX, clickY;
	private InputProcessor processor;
	private List<IPurchaseable> cache;
	private String searchQuery = "";
	private boolean isSearchSelected = false;
	private boolean showCursor = false;
	private float timeUntilShowCursor = 0;

	public ShopWindow(InGameScreen screen, PlayerModel player) {
		this.screen = screen;
		this.player = player;

		recalculateCache();

		processor = new InputProcessor() {
			public boolean keyDown(int keycode) {return false;}
			public boolean keyUp(int keycode) {
				if (keycode == Keys.BACKSPACE && searchQuery.length()>0 && isSearchSelected) {
					searchQuery = searchQuery.substring(0, searchQuery.length()-1);
					recalculateCache();
					return true;
				}
				return false;
			}
			public boolean keyTyped(char character) {
				if ((Character.isLetterOrDigit(character) || character == ' ') && isSearchSelected) {
					searchQuery += character;
					recalculateCache();
					return true;
				}
				return true;
			}
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {return false;}
			public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {return false;}
			public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
			public boolean mouseMoved(int screenX, int screenY) {return false;}
			public boolean scrolled(int amount) {
				if (Gdx.input.getX() <= screen.getWidth() - 131 - infoWidth) {
					double maxAmt = getMaxScrollAmount() - screen.getHeight() + 200;
					mainScrollPos = MathHelper.clamp(0, (maxAmt > 0 ? maxAmt : 0), mainScrollPos + amount*5);
				} else {
					double maxAmt = getSecMaxScrollAmount() - screen.getHeight() + 200;
					secScrollPos = MathHelper.clamp(0, (maxAmt > 0 ? maxAmt : 0), secScrollPos + amount*5);
				}
				return true;
			}
		};

		Client.inputMultiplexer.addProcessor(processor);
	}

	private void recalculateCache() {
		IPurchaseable purch = (cache == null || selected == -1 ? null : cache.get(selected));
		if (searchQuery.isEmpty()) {
			cache = ZombieGame.instance.pluginManager.getPurchaseables();
			cache.sort((p1, p2)->(int)(p1.getPrice(player)-p2.getPrice(player)));
		} else {
			String[] sections = searchQuery.split(" ");
			cache = ZombieGame.instance.pluginManager.getPurchaseables()
					.parallelStream()
					.filter((p)->p.numContained(sections,player)>0)
					.sorted((p1,p2)->{
						int countDiff = p2.numContained(sections, player)-p1.numContained(sections, player);
						if (countDiff != 0) return countDiff;
						double priceDiff = p1.getPrice(player)-p2.getPrice(player);
						return (int) Math.signum(priceDiff);
					})
					.collect(Collectors.toList());
		}
		if (purch == null) {
			selected = -1;
		} else {
			selected = cache.indexOf(purch);
		}
	}

	@Override
	public void drawScreen(float delta, Camera camera) {

		if (timeUntilShowCursor <= 0) {
			timeUntilShowCursor = 0.4f;
			showCursor = !showCursor;
		}
		timeUntilShowCursor -= delta;

		drawPrepare(delta);

		clickX = Gdx.input.getX();
		clickY = Gdx.input.getY();

		drawMainRegion(delta);

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	public void drawPrepare(float delta) {
		infoWidth = defInfoWidth;

		shapeRender.setProjectionMatrix(screen.getScreenProjectionMatrix());
		batch.setProjectionMatrix(screen.getScreenProjectionMatrix());
		batch.setColor(new Color(1, 1, 1, 1));

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRender.begin(ShapeType.Filled);

		//Gray the entire screen
		shapeRender.setColor(0.5f, 0.5f, 0.5f, 0.2f);
		shapeRender.rect(0, 0, screen.getWidth(), screen.getHeight());

		//Main box in the center
		shapeRender.setColor(0.3f, 0.3f, 0.3f, 0.6f);
		shapeRender.rect(100, 100, screen.getWidth()-200, screen.getHeight()-200);

		shapeRender.end();

		shapeRender.begin(ShapeType.Line);

		shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
		//Main box border
		shapeRender.rect(100, 100, screen.getWidth()-200, screen.getHeight()-200);

		shapeRender.end();
	}

	public void drawMainRegion(float delta) {
		float x = 101;
		float y = (float) (screen.getHeight() - 101 + mainScrollPos - 50);
		float w = screen.getWidth() - 232 - infoWidth - 22;

		shapeRender.begin(ShapeType.Filled);
		//Main Scroll bar box
		shapeRender.setColor(0.4f, 0.4f, 0.4f, 1f);
		shapeRender.rect(x+w, 100, 20, screen.getHeight()-200);

		//Main Scroll Bar
		shapeRender.setColor(0.7f, 0.7f, 0.7f, 1f);
		shapeRender.rect(x+w,getScrollBarPos()
				, 20, getScrollBarHeight());
		shapeRender.end();

		shapeRender.begin(ShapeType.Line);

		shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
		//Scroll bar box border left line
		shapeRender.line(x+w, 100, x+w, screen.getHeight()-100);

		shapeRender.end();

		ScissorStack.pushScissors(new Rectangle(x, screen.getHeight() - 201+50 , w, 50));
		{
			shapeRender.begin(ShapeType.Filled);
			shapeRender.setColor(0.4f, 0.4f, 0.4f, 1f);
			shapeRender.rect(x+5, screen.getHeight() - 201+50, w-12, 48);
			shapeRender.end();

			shapeRender.begin(ShapeType.Line);
			shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
			shapeRender.rect(x+5, screen.getHeight() - 201+50, w-12, 48);
			shapeRender.end();

			batch.begin();
			glyph.setText(ZombieGame.instance.mainFont, searchQuery + (showCursor && isSearchSelected ? "|" : ""), Color.WHITE, w-24, Align.left, false);
			ZombieGame.instance.mainFont.draw(batch, glyph, x+10, y + 50/2 + glyph.height/2);
			batch.end();
		}
		ScissorStack.popScissors();

		if (Gdx.input.isTouched() && Gdx.input.justTouched()) {
			if (Gdx.input.getX()>=x && Gdx.input.getX()<=x+w && screen.getHeight()-clickY>=screen.getHeight()-201+50 && screen.getHeight()-clickY<=screen.getHeight()-201+100) {
				isSearchSelected = !isSearchSelected;
			} else {
				isSearchSelected = false;
			}
		}

		boolean clickOnPurchaseable = false;
		if (Gdx.input.isTouched()) {
			if (clickX >= x && clickX <= x + w) {
				if (clickY >= 100 && clickY <= screen.getHeight()-100) {
					clickOnPurchaseable = true;
				}
			}
		}

		if (Gdx.input.isTouched() && clickOnPurchaseable == false && clickX < x && clickX > screen.getWidth() - 232 && clickY < 100 && clickY > screen.getHeight()-100) {
			selected = -1;
		}

		float trueWeaponBoxSize = purchaseableHeight + purchaseableBorder + purchaseableBorder;
		int cols = ((int)((w)/(trueWeaponBoxSize)));

		int mouseOverIndex = -1;

		Texture upgradeArrow = TextureRegistry.getTexturesFor("upgradeArrow").get(0);

		ScissorStack.pushScissors(new Rectangle(x, 100, w, screen.getHeight() - 201-50));
		//Draw Weapons
		for (int i = 0; i<cache.size(); i++) {
			Gdx.gl.glEnable(GL20.GL_BLEND);

			IPurchaseable purchaseable = cache.get(i);

			int row = i/cols;
			int col = i%cols;

			float boxX = trueWeaponBoxSize * col + purchaseableBorder + x;
			float boxY =  y - (trueWeaponBoxSize * (row + 1));

			shapeRender.begin(ShapeType.Filled);

			if (purchaseable.getCurrentLevel(player) >= 0) {
				shapeRender.setColor(new Color(191/255f, 191/255f, 191/255f, 100/255f));
			} else {
				shapeRender.setColor(new Color(95/255f, 95/255f, 95/255f, 100/255f));
			}
			shapeRender.rect(boxX,boxY, purchaseableHeight, purchaseableHeight);

			shapeRender.end();

			if (purchaseable.hasIcon()) {
				batch.begin();
				Texture icon = TextureRegistry.getTexturesFor(purchaseable.getIconName(player)).get(0);

				batch.setColor(new Color(1, 1, 1, 1));
				batch.draw(icon, boxX,boxY, purchaseableHeight, purchaseableHeight);

				batch.end();
			}

			Gdx.gl.glLineWidth(2);
			shapeRender.begin(ShapeType.Line);

			shapeRender.setColor(Color.DARK_GRAY);
			shapeRender.rect(boxX,boxY, purchaseableHeight, purchaseableHeight);

			shapeRender.end();
			Gdx.gl.glLineWidth(1);

			if (purchaseable.canPurchase(player) && player.getMoney() >= purchaseable.getPrice(player)) {
				batch.begin();
				batch.draw(upgradeArrow, boxX + purchaseableHeight*3/4, boxY, purchaseableHeight/4, purchaseableHeight/4);
				batch.end();
			}

			if (clickX > boxX && Gdx.graphics.getHeight() - clickY > boxY &&
					clickX < boxX + purchaseableHeight &&
					Gdx.graphics.getHeight() - clickY < boxY + purchaseableHeight) {
				mouseOverIndex = i;
			}
		}

		ScissorStack.popScissors();

		if (selected >= cache.size()) {
			selected = -1;
		}
		if (selected != -1) {
			drawInfoRegion(delta);
		}

		if (mouseOverIndex != -1) {
			IPurchaseable purchaseable = cache.get(mouseOverIndex);

			if (Gdx.input.isTouched()) {
				selected = mouseOverIndex;
			}			

			float boxWidth = 0;
			float boxHeight = 0;

			//Box Size Calculation

			GlyphLayout nameGlyph = new GlyphLayout(ZombieGame.instance.mainFont, purchaseable.getName(),
					0, purchaseable.getName().length(), Color.BLACK,
					300, Align.left, false, "...");

			boxWidth = Math.max(boxWidth, nameGlyph.width + 8);
			boxHeight += nameGlyph.height + 4 + 4;

			GlyphLayout descGlyph = new GlyphLayout(ZombieGame.instance.mainFont, purchaseable.getDescription(),
					Color.BLACK, 300, Align.left, true);

			boxWidth = Math.max(boxWidth, descGlyph.width + 8);
			boxHeight += descGlyph.height + 4;

			boxHeight += 10;

			GlyphLayout owned = null;
			if (purchaseable.getCurrentLevel(player) > -1 ) {
				owned = new GlyphLayout(ZombieGame.instance.mainFont, "Owned",
						Color.BLUE, 300, Align.left, true);

				boxWidth = Math.max(boxWidth, owned.width + 8);
				boxHeight += descGlyph.height + 4;

				boxHeight += 10;
			}

			GlyphLayout canPurchase = null;
			if (purchaseable.canPurchase(player) && purchaseable.getPrice(player) <= player.getMoney()) {
				canPurchase = new GlyphLayout(ZombieGame.instance.mainFont, "Purchase Available",
						new Color(27/255f, 168/255f, 55/255f, 1f), 300, Align.left, true);

				boxWidth = Math.max(boxWidth, canPurchase.width + 8);
				boxHeight += descGlyph.height + 4;

				boxHeight += 10;
			}

			boxHeight += 10;

			//Box Outline Drawing

			shapeRender.begin(ShapeType.Filled);

			shapeRender.setColor(Color.LIGHT_GRAY);
			shapeRender.rect(clickX, screen.getHeight() - clickY - boxHeight, boxWidth, boxHeight);

			shapeRender.end();

			shapeRender.begin(ShapeType.Line);

			shapeRender.setColor(Color.DARK_GRAY);
			shapeRender.rect(clickX, screen.getHeight() - clickY - boxHeight, boxWidth, boxHeight);

			shapeRender.end();

			batch.begin();

			//Text Drawing

			float textDrawing = screen.getHeight() - clickY - 3;

			ZombieGame.instance.mainFont.draw(batch, nameGlyph, clickX + 4, textDrawing);
			textDrawing -= nameGlyph.height + 4;

			textDrawing -= 10;

			ZombieGame.instance.mainFont.draw(batch, descGlyph, clickX + 4, textDrawing);
			textDrawing -= descGlyph.height + 4;

			if (owned != null) {
				textDrawing -= 10;

				ZombieGame.instance.mainFont.draw(batch, owned, clickX + 4, textDrawing);
				textDrawing -= owned.height + 4;
			}

			if (canPurchase != null) {
				textDrawing -= 10;

				ZombieGame.instance.mainFont.draw(batch, canPurchase, clickX + 4, textDrawing);
				textDrawing -= canPurchase.height + 4;
			}

			batch.end();
		}
	}

	public void drawInfoRegion(float delta) {
		float x = screen.getWidth() - 111 - infoWidth;
		infoWidth -= 20;
		float y = (float) (screen.getHeight() - 106 + secScrollPos);
		float startY = y;

		float wMult = (float)Gdx.graphics.getWidth()/screen.getWidth();
		float hMult = (float)Gdx.graphics.getHeight()/screen.getHeight();

		Rectangle clipBounds = new Rectangle(x*wMult, 100*hMult, infoWidth*wMult, (screen.getHeight() - 200)*hMult);
		ScissorStack.pushScissors(clipBounds);

		IPurchaseable selectedItem = cache.get(selected);
		batch.begin();

		glyph.setText(ZombieGame.instance.mainFont, selectedItem.getName(), 0, selectedItem.getName().length(), new Color(1,1,1,1), infoWidth - 20, Align.left, false, "...");
		ZombieGame.instance.mainFont.draw(batch, glyph, x, y); y -= glyph.height + 20;

		if (selectedItem.getDescription() != null) {
			glyph.setText(ZombieGame.instance.mainFont, selectedItem.getDescription(), new Color(1,1,1,1), infoWidth - 20, Align.left, true);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y); y -= glyph.height + 20;
		}

		Map<String, String> currLev = selectedItem.getCurrentDescriptors(player);
		Map<String, String> nextLev = selectedItem.getNextDescriptors(player);

		if (currLev != null) {
			for (String key : currLev.keySet()) {
				String curr = currLev.get(key);
				String next = (nextLev == null ? null : nextLev.get(key));

				float thisX = x;

				float maxH = 0;

				glyph.setText(ZombieGame.instance.mainFont, key + ": ", new Color(1,1,1,1), infoWidth - 20, Align.left, true);
				maxH = Math.max(maxH, glyph.height);
				ZombieGame.instance.mainFont.draw(batch, glyph, thisX, y); thisX += glyph.width;

				glyph.setText(ZombieGame.instance.mainFont, curr + (next == null ? "" : " -> "), new Color(1,1,1,1), infoWidth - 20, Align.left, true);
				maxH = Math.max(maxH, glyph.height);
				ZombieGame.instance.mainFont.draw(batch, glyph, thisX, y); thisX += glyph.width;

				if (next != null) {
					glyph.setText(ZombieGame.instance.mainFont, next, Color.GREEN, infoWidth - 20, Align.left, true);
					maxH = Math.max(maxH, glyph.height);
					ZombieGame.instance.mainFont.draw(batch, glyph, thisX, y); thisX += glyph.width;
				}

				y -= maxH + 5;
			}
		}

		if (selectedItem.hasNextLevel(player)) {
			float w = infoWidth - 40;
			float h = 50;
			x+=10;
			y-=60;

			double price = selectedItem.getPrice(player);

			boolean hasEnoughMoney = player.getMoney() >= price;

			if (!hasEnoughMoney) {
				batch.setColor(Color.GRAY);
			}
			//batch.draw(ZombieGame.instance.buttonTexture, x, y, w, h);
			batch.setColor(new Color(1,1,1,1));

			glyph.setText(ZombieGame.instance.mainFont, (selectedItem.getCurrentLevel(player)>-1 ? "Upgrade" : "Buy"), (!hasEnoughMoney ? Color.DARK_GRAY :
				(clickX >= x && clickX <= x+w && screen.getHeight()-clickY >= y && screen.getHeight()-clickY <= y+h? new Color(27/255f, 168/255f, 55/255f, 1f) :Color.WHITE))
					, w, Align.center, true);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + h/2 + glyph.height/2);

			if (hasEnoughMoney && Gdx.input.isTouched() && Gdx.input.justTouched()) {
				if (clickX >= x && clickX <= x + w) {
					if (screen.getHeight() - clickY >= y && screen.getHeight() - clickY <= y + h) {
						selectedItem.onPurchase(player);
						player.setMoney(player.getMoney() - price);
						recalculateCache();
					}
				}
			}

			x-=10;
			y-=10;
		}

		batch.end();
		ScissorStack.popScissors();

		if (y < 100) {
			//Secondary Scroll bar box
			shapeRender.begin(ShapeType.Filled);
			shapeRender.setColor(0.4f, 0.4f, 0.4f, 1f);
			shapeRender.rect(screen.getWidth()-120, 100, 20, screen.getHeight()-200);

			//Secondary Scroll Bar
			shapeRender.setColor(0.7f, 0.7f, 0.7f, 1f);
			shapeRender.rect(screen.getWidth()-120, getSecScrollBarPos()
					, 20, getSecScrollBarHeight());

			shapeRender.end();
			shapeRender.begin(ShapeType.Line);

			shapeRender.setColor(0.9f, 0.9f, 0.9f, 0.5f);
			//Scroll bar box border left line
			shapeRender.line(screen.getWidth()-120, 100, screen.getWidth()-120, screen.getHeight()-100);

			shapeRender.end();
		}

		secHeight = startY - y;
	}

	private double getMaxScrollAmount() {
		return (purchaseableHeight/((int)((screen.getWidth() - 232 - infoWidth - 22)/(purchaseableHeight + purchaseableBorder + purchaseableBorder))) + 2) * cache.size();
		//return (purchaseableHeight + 2) * ZombieGame.instance.pluginManager.getPurchaseables().size();
	}

	private double getSecMaxScrollAmount() {
		return secHeight;
	}

	private float getScrollBarPos() {
		double screenHeight = screen.getHeight()-200;
		double pos = (screenHeight - getScrollBarHeight()) * (mainScrollPos)/(getMaxScrollAmount() - screenHeight);
		pos = (screen.getHeight() - 100) - pos - getScrollBarHeight();
		//double pos = (getMaxScrollAmount() - screenHeight - scrollPos + 47);
		//if (pos < 100) {
		//	pos = 100;
		//}
		return (float) pos;
	}

	private float getSecScrollBarPos() {
		double screenHeight = screen.getHeight()-200;
		double pos = (screenHeight - getSecScrollBarHeight()) * (secScrollPos)/(getSecMaxScrollAmount() - screenHeight);
		pos = (screen.getHeight() - 100) - pos - getSecScrollBarHeight();
		return (float) pos;
	}

	private float getScrollBarHeight() {
		double screenHeight = screen.getHeight()-200;
		double height = (screenHeight * screenHeight)/getMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}

	private float getSecScrollBarHeight() {
		double screenHeight = screen.getHeight()-200;
		double height = (screenHeight * screenHeight)/getSecMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}

	@Override
	public void dispose() {
		shapeRender.dispose();
		batch.dispose();
		Client.inputMultiplexer.removeProcessor(processor);
	}
}
