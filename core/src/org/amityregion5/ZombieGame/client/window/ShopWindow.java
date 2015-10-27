package org.amityregion5.ZombieGame.client.window;

import java.util.Map;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.shop.IPurchaseable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
	private int selected = -1;
	private float defInfoWidth = 300;
	private float infoWidth = defInfoWidth;
	private float secHeight = 0;
	private int clickX, clickY;

	public ShopWindow(InGameScreen screen, PlayerModel player) {
		this.screen = screen;
		this.player = player;

		Gdx.input.setInputProcessor(new InputProcessor() {
			public boolean keyDown(int keycode) {return false;}
			public boolean keyUp(int keycode) {return false;}
			public boolean keyTyped(char character) {return false;}
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
		});
	}

	@Override
	public void drawScreen(float delta, Camera camera) {
		drawPrepare(delta);

		clickX = Gdx.input.getX();
		clickY = Gdx.input.getY();

		drawMainRegion(delta);

		if (selected != -1) {
			drawInfoRegion(delta);
		}

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
		shapeRender.setColor(0.3f, 0.3f, 0.3f, 1f);
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
		float y = (float) (screen.getHeight() - 101 + mainScrollPos);
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

		Rectangle clipBounds = new Rectangle(x, 100, w, screen.getHeight() - 201);
		//ScissorStack.calculateScissors(camera, screen.getScreenProjectionMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(clipBounds);
		//Draw Weapons
		for (int i = 0; i<ZombieGame.instance.pluginManager.getPurchaseables().size(); i++) {
			IPurchaseable purchaseable = ZombieGame.instance.pluginManager.getPurchaseables().get(i);
			y -= purchaseableHeight + 2;

			if (clickOnPurchaseable) {
				int checkClickY = screen.getHeight() - clickY;
				if (checkClickY >= y && checkClickY <= y + purchaseableHeight+2) {
					if (selected != i) {
						secScrollPos = 0;
					}
					selected = i;
				}
			}

			shapeRender.begin(ShapeType.Filled);
			if (selected == i) {
				shapeRender.setColor(0.6f, 0.6f, 0.6f, 1f);
			} else {
				shapeRender.setColor(0.35f, 0.35f, 0.35f, 1f);
			}
			shapeRender.rect(x, y, w, purchaseableHeight);
			shapeRender.end();

			shapeRender.begin(ShapeType.Line);
			shapeRender.setColor(0.45f, 0.45f, 0.45f, 1f);
			shapeRender.rect(x, y, w, purchaseableHeight);
			shapeRender.end();

			batch.begin();
			// Get the size of the text
			glyph.setText(ZombieGame.instance.mainFont, purchaseable.getName(), 0, purchaseable.getName().length(), Color.BLACK, w - 20, Align.left, false, "...");
			// Draw the text centered on the button
			ZombieGame.instance.mainFont.draw(batch, glyph, x + 10, y + purchaseableHeight - 10 + glyph.height/2);

			if (purchaseable.hasIcon()) {
				Texture icon = TextureRegistry.getTexturesFor(purchaseable.getIconName(player)).get(0);

				batch.draw(icon, x + 10, y + 10, 64, 64);
			}

			batch.end();
		}

		ScissorStack.popScissors();
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

		IPurchaseable selectedItem = ZombieGame.instance.pluginManager.getPurchaseables().get(selected);
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
			batch.draw(ZombieGame.instance.buttonTexture, x, y, w, h);
			batch.setColor(new Color(1,1,1,1));

			glyph.setText(ZombieGame.instance.mainFont, (selectedItem.getCurrentLevel(player)>-1 ? "Upgrade" : "Buy"), Color.BLACK, w, Align.center, true);
			ZombieGame.instance.mainFont.draw(batch, glyph, x, y + h/2 + glyph.height/2);

			if (hasEnoughMoney && Gdx.input.isTouched() && Gdx.input.justTouched()) {
				if (clickX >= x && clickX <= x + w) {
					if (screen.getHeight() - clickY >= y && screen.getHeight() - clickY <= y + h) {
						selectedItem.onPurchase(player);
						player.setMoney(player.getMoney() - price);
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
		return (purchaseableHeight + 2) * ZombieGame.instance.pluginManager.getPurchaseables().size();
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
	}
}
