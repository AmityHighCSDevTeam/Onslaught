package org.amityregion5.onslaught.client.game;

import org.amityregion5.onslaught.client.asset.TextureRegistry;
import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.game.model.IParticle;
import org.amityregion5.onslaught.common.game.model.particle.HealthPackParticle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class HealthPackDrawingLayer implements IDrawingLayer {

	public static final HealthPackDrawingLayer instance = new HealthPackDrawingLayer();

	private Sprite sprite;

	public HealthPackDrawingLayer() {
		sprite = TextureRegistry.getAtlas().createSprite(TextureRegistry.getTextureNamesFor("healthPack").get(0));
		//sprite = new Sprite(TextureRegistry.getTexturesFor("healthPack").get(0));
	}

	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {
		HealthPackParticle model = (HealthPackParticle) p;
		Color c = batch.getColor();

		sprite.setOriginCenter();
		// sprite.setRotation((float) (-90));
		sprite.setBounds(model.getX() - model.getSize(), model.getY() - model.getSize(), model.getSize() * 2, model.getSize() * 2);

		if (cullRect.overlaps(sprite.getBoundingRectangle())) {
			sprite.draw(batch);
		}
		batch.setColor(c);
	}
}
