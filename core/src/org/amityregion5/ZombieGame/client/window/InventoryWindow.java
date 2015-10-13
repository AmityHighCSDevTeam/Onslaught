package org.amityregion5.ZombieGame.client.window;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.game.model.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
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

public class InventoryWindow implements Screen {
	private ShapeRenderer shapeRender = new ShapeRenderer();
	private InGameScreen screen;
	private PlayerModel player;
	private double scrollPos;
	private SpriteBatch batch = new SpriteBatch();
	private float weaponBoxSize = 128;
	private float weaponBoxBorder = 8;
	private int mouseX, mouseY;
	
	public InventoryWindow(InGameScreen screen, PlayerModel player) {
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
					double maxAmt = getMaxScrollAmount() - screen.getHeight() + 200;
					scrollPos = MathHelper.clamp(0, (maxAmt > 0 ? maxAmt : 0), scrollPos + amount*5);
				return true;
			}
		});
	}

	@Override
	public void drawScreen(float delta, Camera camera) {
		drawPrepare(delta);
		
		mouseX = Gdx.input.getX();
		mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
		
		drawMain(delta);
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	private void drawPrepare(float delta) {
		shapeRender.setProjectionMatrix(screen.getScreenProjectionMatrix());
		batch.setProjectionMatrix(screen.getScreenProjectionMatrix());

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
	
	private void drawMain(float delta){
		float x = 101;
		float y = (float) (screen.getHeight() - 101 + scrollPos);
		float w = screen.getWidth() - 221;

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
		
		int cols = ((int)((screen.getWidth() - 221)/(weaponBoxSize + weaponBoxBorder + weaponBoxBorder)));
		
		float trueWeaponBoxSize = weaponBoxSize + weaponBoxBorder + weaponBoxBorder;
		
		float wMult = (float)Gdx.graphics.getWidth()/screen.getWidth();
		float hMult = (float)Gdx.graphics.getHeight()/screen.getHeight();
		
		int mouseOverIndex = -1;
		
		Rectangle clipBounds = new Rectangle(x*wMult, 100*hMult, w*wMult, (screen.getHeight() - 201)*hMult);
		ScissorStack.pushScissors(clipBounds);
		for (int i = 0; i<player.getWeapons().size(); i++) {
			int row = i/cols;
			int col = i%cols;
			
			float boxX = trueWeaponBoxSize * col + weaponBoxBorder + x;
			float boxY =  y - (trueWeaponBoxSize * (row + 1));
			
			shapeRender.begin(ShapeType.Filled);
			
			shapeRender.setColor(Color.LIGHT_GRAY);
			shapeRender.rect(boxX,boxY, weaponBoxSize, weaponBoxSize);
			
			shapeRender.end();
			
			WeaponStack weapon = player.getWeapons().get(i);
			
			batch.begin();
			Texture icon = TextureRegistry.getTexturesFor(weapon.getIconTextureName()).get(0);

			batch.setColor(new Color(1, 1, 1, 1));
			batch.draw(icon, boxX,boxY, weaponBoxSize, weaponBoxSize);
			
			batch.end();
			
			Gdx.gl.glLineWidth(2);
			shapeRender.begin(ShapeType.Line);
			
			shapeRender.setColor(Color.DARK_GRAY);
			shapeRender.rect(boxX,boxY, weaponBoxSize, weaponBoxSize);
			
			shapeRender.end();
			Gdx.gl.glLineWidth(1);

			if (mouseX > boxX && mouseY > boxY &&
					mouseX < boxX + weaponBoxSize &&
					mouseY < boxY + weaponBoxSize) {
				mouseOverIndex = i;
			}
		}
		ScissorStack.popScissors();
		
		if (mouseOverIndex != -1) {
			WeaponStack weapon = player.getWeapons().get(mouseOverIndex);
			
			if (Gdx.input.isTouched()) {
				player.getHotbar()[player.getCurrWeapIndex()] = weapon;
			}
			
			float boxWidth = 0;
			float boxHeight = 0;
			
			GlyphLayout nameGlyph = new GlyphLayout(ZombieGame.instance.mainFont, weapon.getWeapon().getName(),
					0, weapon.getWeapon().getName().length(), Color.BLACK,
					300, Align.left, false, "...");
			
			boxWidth = Math.max(boxWidth, nameGlyph.width + 8);
			boxHeight += nameGlyph.height + 4 + 4;
			
			GlyphLayout descGlyph = new GlyphLayout(ZombieGame.instance.mainFont, weapon.getWeapon().getDescription(),
					Color.BLACK, 300, Align.left, true);
			
			boxWidth = Math.max(boxWidth, descGlyph.width + 8);
			boxHeight += descGlyph.height + 4;
			
			boxHeight += 10;
			
			shapeRender.begin(ShapeType.Filled);
			
			shapeRender.setColor(Color.LIGHT_GRAY);
			shapeRender.rect(mouseX, mouseY - boxHeight, boxWidth, boxHeight);
			
			shapeRender.end();
			
			shapeRender.begin(ShapeType.Line);
			
			shapeRender.setColor(Color.DARK_GRAY);
			shapeRender.rect(mouseX, mouseY - boxHeight, boxWidth, boxHeight);
			
			shapeRender.end();
			
			batch.begin();
			
			float textDrawing = mouseY - 3;
			
			ZombieGame.instance.mainFont.draw(batch, nameGlyph, mouseX + 4, textDrawing);
			textDrawing -= nameGlyph.height + 4;
			
			textDrawing -= 10;
			
			ZombieGame.instance.mainFont.draw(batch, descGlyph, mouseX + 4, textDrawing);
			textDrawing -= descGlyph.height + 4;
			
			batch.end();
		} else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			player.getHotbar()[player.getCurrWeapIndex()] = new WeaponStack(new NullWeapon());
		}
	}

	@Override
	public void dispose() {
	}
	
	private double getMaxScrollAmount() {
		return (weaponBoxSize/((int)((screen.getWidth() - 221)/(weaponBoxSize + weaponBoxBorder + weaponBoxBorder))) + 2) * ZombieGame.instance.weaponRegistry.getWeapons().size();
	}

	private float getScrollBarPos() {
		double screenHeight = screen.getHeight()-200;
		double pos = (screenHeight - getScrollBarHeight()) * (scrollPos)/(getMaxScrollAmount() - screenHeight);
		pos = (screen.getHeight() - 100) - pos - getScrollBarHeight();
		return (float) pos;
	}

	private float getScrollBarHeight() {
		double screenHeight = screen.getHeight()-200;
		double height = (screenHeight * screenHeight)/getMaxScrollAmount();
		return (float) (height > screenHeight ? screenHeight : height);
	}
}
