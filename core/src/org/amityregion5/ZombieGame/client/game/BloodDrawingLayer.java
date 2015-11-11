package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.game.model.particle.BloodParticle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BloodDrawingLayer implements IDrawingLayer {
	
	public static final BloodDrawingLayer instance = new BloodDrawingLayer();
	
	public BloodDrawingLayer() {
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer) {}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		BloodParticle model = (BloodParticle) p;
		Color c = batch.getColor();
		batch.begin();
		
		Sprite sprite = new Sprite(TextureRegistry.getTexturesFor(model.getTextureName()).get(0));
		
		sprite.setOriginCenter();
		sprite.setRotation((float) Math.toDegrees(model.getR()));
		sprite.setAlpha(model.getOpacity());
		sprite.setScale(model.getSize());
		sprite.setCenter(model.getX(), model.getY());
		//sprite.setBounds(model.getX()
		//		- model.getSize(), model.getY() - model.getSize(),
		//		model.getSize() * 2,
		//		model.getSize() * 2);

		sprite.draw(batch);
		batch.end();
		batch.setColor(c);
	}
}
