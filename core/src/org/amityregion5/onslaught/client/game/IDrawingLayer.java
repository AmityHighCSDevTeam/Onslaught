package org.amityregion5.onslaught.client.game;

import org.amityregion5.onslaught.common.game.model.IEntityModel;
import org.amityregion5.onslaught.common.game.model.IParticle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * An interface for a drawing layer
 * @author sergeys
 *
 */
public interface IDrawingLayer {
	/**
	 * Draw an entity model. The sprite batch is already enabled. Disable before using shapeRenderer.
	 * 
	 * @param e the entity model to draw
	 * @param batch the sprite batch
	 * @param shapeRenderer the shape renderer
	 */
	public void draw(IEntityModel<?> e, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullingRect);

	/**
	 * Draw a particle. The sprite batch is already enabled. Disable before using shapeRenderer.
	 * 
	 * @param p the particle to draw
	 * @param batch the sprite batch
	 * @param shapeRenderer the shape renderer
	 */
	public void draw(IParticle p, SpriteBatch batch, ShapeRenderer shapeRenderer, Rectangle cullingRect);
}
