package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.game.model.ExplosionParticleModel;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ExplosionParticleDrawingLayer implements IDrawingLayer {
	
	public static final ExplosionParticleDrawingLayer instance = new ExplosionParticleDrawingLayer();
	
	private Sprite sprite;
	
	private float maxSize = 0.15f;
	
	public ExplosionParticleDrawingLayer() {
		this.sprite = new Sprite(TextureRegistry.getTexturesFor("explosion").get(0));
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		IEntity e = em.getEntity();
		ExplosionParticleModel model = (ExplosionParticleModel) em;
		Color c = batch.getColor();
		batch.begin();
		
		batch.setColor(new Color(model.getColor().r, model.getColor().g, model.getColor().b, 1));
		
		float val = (float) Math.sqrt(model.getLight().getColor().r);
		
		sprite.setOriginCenter();
		sprite.setRotation((float) (Math.toDegrees(e.getBody().getAngle()) - 90));
		sprite.setAlpha(val * (float)Math.sqrt(val) * 1.5f);
		sprite.setBounds(e.getBody().getWorldCenter().x
				- (maxSize / val), e.getBody()
				.getWorldCenter().y - (maxSize / val),
				maxSize / val * 2,
				maxSize / val * 2);

		sprite.draw(batch);
		batch.end();
		batch.setColor(c);
	}
}
