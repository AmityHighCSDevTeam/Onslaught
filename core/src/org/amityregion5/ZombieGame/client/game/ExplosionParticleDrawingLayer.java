package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.game.model.ExplosionParticleModel;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ExplosionParticleDrawingLayer implements IDrawingLayer {
	
	public static final ExplosionParticleDrawingLayer instance = new ExplosionParticleDrawingLayer();
	
	private Sprite sprite;
	
	private float maxSize = 1f;
	
	public ExplosionParticleDrawingLayer() {
		this.sprite = new Sprite(TextureRegistry.getTexturesFor("Core/explosion_smoke_noAlpha.png").get(0));
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		IEntity e = em.getEntity();
		ExplosionParticleModel model = (ExplosionParticleModel) em;
		batch.begin();
		sprite.setOriginCenter();
		sprite.setRotation((float) (Math.toDegrees(e.getBody().getAngle()) - 90));
		sprite.setBounds(e.getBody().getWorldCenter().x
				- (maxSize * model.getLight().getColor().r), e.getBody()
				.getWorldCenter().y - (maxSize * model.getLight().getColor().r),
				maxSize * model.getLight().getColor().r * 2,
				maxSize * model.getLight().getColor().r * 2);

		sprite.draw(batch);
		batch.end();
	}
}
