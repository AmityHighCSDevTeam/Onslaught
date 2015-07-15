package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SpriteDrawingLayer implements IDrawingLayer {
	
	private Sprite sprite;
	
	public SpriteDrawingLayer(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public void draw(IEntityModel<?> em, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		IEntity e = em.getEntity();
		batch.begin();
		sprite.setRotation((float) (Math.toDegrees(e.getBody().getAngle()) - 90));
		sprite.setBounds(e.getBody().getWorldCenter().x
				- e.getShape().getRadius(), e.getBody()
				.getWorldCenter().y - e.getShape().getRadius(), e
				.getShape().getRadius() * 2,
				e.getShape().getRadius() * 2);

		sprite.draw(batch);
		batch.end();
	}
}
