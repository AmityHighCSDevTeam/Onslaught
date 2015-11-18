package org.amityregion5.ZombieGame.client.window;

import java.text.NumberFormat;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

public class HUDOverlay implements Screen {
	private ShapeRenderer	shapeRender	= new ShapeRenderer();
	private InGameScreen	screen;
	private PlayerModel		player;
	private GlyphLayout		glyph		= new GlyphLayout();
	private SpriteBatch		batch		= new SpriteBatch();
	private int				eachBoxSize	= 64;

	public HUDOverlay(InGameScreen screen, PlayerModel player) {
		this.screen = screen;
		this.player = player;
	}

	@Override
	public void drawScreen(float delta, Camera camera) {
		drawPrepare(delta);

		drawHotbar(delta);

		drawLeftHUD(delta);

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

	public void drawHotbar(float delta) {
		float startX = (screen.getWidth() - player.getHotbar().length * eachBoxSize) / 2;
		for (int i = 0; i < player.getHotbar().length; i++) {
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

	private void drawLeftHUD(float delta) {

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRender.setProjectionMatrix(batch.getProjectionMatrix());

		shapeRender.begin(ShapeType.Filled);

		shapeRender.setColor(75 / 255f, 75 / 255f, 75 / 255f, 75 / 255f);
		shapeRender.rect(screen.getWidth() - 400, 0, 400, 200);
		shapeRender.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		batch.begin();
		screen.getFont1().draw(batch, player.getCurrentWeapon().getWeapon().getName(), screen.getWidth() - 390, 190);
		if (player.getCurrentWeapon().getTotalAmmo() == 0 ^ player.getCurrentWeapon().getAmmo() == 0) {
			screen.getFont1().setColor(Color.YELLOW);
		}
		if (player.getCurrentWeapon().getTotalAmmo() == 0 && player.getCurrentWeapon().getAmmo() == 0) {
			screen.getFont1().setColor(Color.RED);
		}
		screen.getFont1().draw(batch, player.getCurrentWeapon().getAmmoString(), screen.getWidth() - 390, 170);
		screen.getFont1().setColor(new Color(1, 1, 1, 1));
		screen.getFont1().draw(batch, "$" + NumberFormat.getInstance().format(player.getMoney()), screen.getWidth() - 390, 150);
		batch.end();

		shapeRender.begin(ShapeType.Filled);

		shapeRender.setColor(1f, 0f, 0f, 1f);
		shapeRender.rect(screen.getWidth() - 395, 110, 90, 20);

		shapeRender.setColor(0f, 1f, 0f, 1f);
		shapeRender.rect(screen.getWidth() - 395, 110, 90 * player.getHealth() / player.getMaxHealth(), 20);

		shapeRender.end();

		batch.begin();
		glyph.setText(screen.getFont2(), ((int) (player.getHealth() / player.getMaxHealth() * 100 * 10 + 0.5)) / 10f + "%", new Color(1, 1, 1, 1), 90,
				Align.center, false);
		screen.getFont2().draw(batch, glyph, screen.getWidth() - 395/* + glyph.width/2 */, 113 + glyph.height);
		batch.end();
	}

	@Override
	public void dispose() {
		shapeRender.dispose();
		batch.dispose();
	}
}
