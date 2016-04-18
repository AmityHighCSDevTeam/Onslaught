package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class BloodDrawingLayer implements IDrawingLayer {

	public static final BloodDrawingLayer instance = null;// = new BloodDrawingLayer();

	public BloodDrawingLayer() {}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {/*
		BloodParticle model = (BloodParticle) p;
		Color c = batch.getColor();
		
		String txtr = TextureRegistry.getTextureNamesFor(model.getTextureName()).get(0);

		Sprite sprite = new Sprite(TextureRegistry.getTexturesFor(model.getTextureName()).get(0));

		sprite.setOriginCenter();
		sprite.setRotation((float) Math.toDegrees(model.getR()));
		sprite.setAlpha(model.getOpacity());
		sprite.setScale(model.getSize());
		sprite.setCenter(model.getX(), model.getY());
		
		if (cullRect.overlaps(sprite.getBoundingRectangle())) {
			sprite.draw(batch);
		}
		// sprite.setBounds(model.getX()
		// - model.getSize(), model.getY() - model.getSize(),
		// model.getSize() * 2,
		// model.getSize() * 2);

		batch.setColor(c);*/
	}
}
