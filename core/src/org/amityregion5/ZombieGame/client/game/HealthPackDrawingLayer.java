package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.particle.HealthPackParticle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HealthPackDrawingLayer implements IDrawingLayer {

	public static final HealthPackDrawingLayer instance = new HealthPackDrawingLayer();

	private Sprite sprite;

	public HealthPackDrawingLayer() {
		sprite = new Sprite(TextureRegistry.getTexturesFor("healthPack").get(0));
	}

	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer) {}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		HealthPackParticle model = (HealthPackParticle) p;
		Color c = batch.getColor();
		batch.begin();

		sprite.setOriginCenter();
		// sprite.setRotation((float) (-90));
		sprite.setBounds(model.getX() - model.getSize(), model.getY() - model.getSize(), model.getSize() * 2,
				model.getSize() * 2);

		sprite.draw(batch);
		batch.end();
		batch.setColor(c);
	}
}
