package org.amityregion5.ZombieGame.common.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * An interface for defining an entity
 * @author sergeys
 *
 */
public interface IEntity {
	/**
	 * Set the shape of this entity
	 * (This method is not often implemented)
	 * @param e the shape to set
	 */
	public void setShape(Shape e);

	/**
	 * Get the shape of this entity
	 * Must be disposed when you are done using it
	 * @return the shape of this entity
	 */
	public Shape getShape();

	/**
	 * Set this entity's body
	 * Should not be called unless you know what you are doing
	 * @param b the new body
	 */
	public void setBody(Body b);

	/**
	 * Get this entity's body
	 * @return this entity's body
	 */
	public Body getBody();

	/**
	 * Get this entity's friction value (0-1)
	 * @return this entity's friction value
	 */
	public float getFriction();

	/**
	 * Set this entity's friction value (0-1)
	 * @param f the new friction value for this entity
	 */
	public void setFriction(float f);

	/**
	 * Get this entity's MassData
	 * @return this entity's mass data
	 */
	public MassData getMassData();
}
