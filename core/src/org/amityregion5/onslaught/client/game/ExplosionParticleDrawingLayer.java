package org.amityregion5.onslaught.client.game;

import org.amityregion5.onslaught.client.asset.TextureRegistry;
import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.game.model.IParticle;
import org.amityregion5.onslaught.common.game.model.particle.ExplosionParticleModel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class ExplosionParticleDrawingLayer implements IDrawingLayer {

	public static final ExplosionParticleDrawingLayer instance = new ExplosionParticleDrawingLayer();

	private Sprite sprite;

	private float maxSize = 0.15f;

	public ExplosionParticleDrawingLayer() {
		sprite = TextureRegistry.getAtlas().createSprite(TextureRegistry.getTextureNamesFor("explosion").get(0));
		//sprite = new Sprite(TextureRegistry.getTexturesFor("explosion").get(0));
	}

	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {}

	@Override
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullRect) {
		ExplosionParticleModel model = (ExplosionParticleModel) p;
		Color c = batch.getColor();

		batch.setColor(new Color(model.getColor().r, model.getColor().g, model.getColor().b, 1));

		float val = (float) Math.sqrt(model.getSize());

		sprite.setOriginCenter();
		sprite.setRotation((float) (Math.toDegrees(model.getRotation()) - 90));
		sprite.setAlpha(val * (float) Math.sqrt(val) * 1.5f);
		sprite.setBounds(model.getX() - (maxSize / val), model.getY() - (maxSize / val), maxSize / val * 2, maxSize / val * 2);

		if (cullRect.overlaps(sprite.getBoundingRectangle())) {
			sprite.draw(batch);
		}
		batch.setColor(c);
	}
}
