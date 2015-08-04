package org.amityregion5.ZombieGame.client.window;

import org.amityregion5.ZombieGame.client.game.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.game.model.PlayerModel;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class HotbarOverlay implements Screen {
	private ShapeRenderer shapeRender = new ShapeRenderer();
	private InGameScreen screen;
	private PlayerModel player;
	//private GlyphLayout glyph = new GlyphLayout();
	private SpriteBatch batch = new SpriteBatch();
	private int eachBoxSize = 64;

	public HotbarOverlay(InGameScreen screen, PlayerModel player) {
		this.screen = screen;
		this.player = player;
	}

	@Override
	public void drawScreen(float delta, Camera camera) {
		drawPrepare(delta);
		
		drawMain(delta);

		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glLineWidth(1);
	}

	public void drawPrepare(float delta) {		
		shapeRender.setProjectionMatrix(screen.getScreenProjectionMatrix());
		batch.setProjectionMatrix(screen.getScreenProjectionMatrix());

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendColor(1, 1, 1, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void drawMain(float delta) {
		float startX = (screen.getWidth()-player.getHotbar().length*eachBoxSize)/2;
		for (int i = 0; i<player.getHotbar().length; i++) {
			shapeRender.begin(ShapeType.Filled);
			shapeRender.setColor(Color.GRAY);
			shapeRender.rect(startX + eachBoxSize * i, 0, eachBoxSize, eachBoxSize);
			shapeRender.end();
			
			Gdx.gl.glLineWidth(2);
			
			if (!(player.getHotbar()[i].getWeapon() instanceof NullWeapon)) {
				WeaponStack weapon = player.getHotbar()[i];
				
				batch.begin();
				Texture icon = TextureRegistry.getTexturesFor(weapon.getIconTextureName()).get(0);

				batch.setColor(new Color(1, 1, 1, 1));
				batch.draw(icon, startX + eachBoxSize * i, 0, eachBoxSize, eachBoxSize);
				
				batch.end();
			}

			shapeRender.begin(ShapeType.Line);
			shapeRender.setColor(Color.DARK_GRAY);
			shapeRender.rect(startX + eachBoxSize * i, 0, eachBoxSize, eachBoxSize);
			shapeRender.end();
		}

		shapeRender.begin(ShapeType.Line);
		shapeRender.setColor(Color.LIGHT_GRAY);
		shapeRender.rect(startX + eachBoxSize * player.getCurrWeapIndex(), 0, eachBoxSize, eachBoxSize);
		shapeRender.end();
	}

	@Override
	public void dispose() {
		shapeRender.dispose();
		batch.dispose();
	}
}
