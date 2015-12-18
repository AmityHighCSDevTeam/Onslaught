package org.amityregion5.ZombieGame.client.game;

import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.IParticle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * An interface for a drawing layer
 * @author sergeys
 *
 */
public interface IDrawingLayer {
	/**
	 * Draw an entity model
	 * 
	 * @param e the entity model to draw
	 * @param batch the sprite batch
	 * @param shapeRenderer the shape renderer
	 */
	public void draw(IEntityModel<?> e, SpriteBatch batch, ShapeRenderer shapeRenderer);

	/**
	 * Draw a particle
	 * 
	 * @param p the particle to draw
	 * @param batch the sprite batch
	 * @param shapeRenderer the shape renderer
	 */
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer);
}
