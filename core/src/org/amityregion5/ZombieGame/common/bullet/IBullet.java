package org.amityregion5.ZombieGame.common.bullet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

/**
 * An interface defining a bullet
 * @author sergeys
 *
 */
public interface IBullet extends RayCastCallback {
	/**
	 * Set the damage for this bullet
	 * @param damage the damage
	 */
	public void setDamage(float damage);

	/**
	 * Set the knockback for this bullet (in Newtons (kg * m /s^2))
	 * @param knockback the knockback force
	 */
	public void setKnockback(float knockback);

	/**
	 * Get the damage amount
	 * @return the damage amount
	 */
	public float getDamage();

	/**
	 * Get the knockback amount
	 * @return the knockback amount
	 */
	public float getKnockback();

	/**
	 * Set the bullet's direction in radians
	 * @param dir the direction
	 */
	public void setDir(float dir);

	/**
	 * Get the bullet's direction
	 * @return the direction
	 */
	public float getDir();

	/**
	 * Called after the raycast completes.
	 * Do stuff with raycast in here
	 */
	public void finishRaycast();

	/**
	 * Set the bullet's start position
	 * @param start the start position
	 */
	public void setStart(Vector2 start);

	/**
	 * Get the start position
	 * @return the start position
	 */
	public Vector2 getStart();

	/**
	 * Get the end position
	 * @return the end position
	 */
	public Vector2 getEnd();

	/**
	 * Should this bullet be drawn
	 * @return if this bullet should be drawn to the screen
	 */
	public boolean doDraw();

	/**
	 * Get the color of this bullet
	 * @return the color of this bullet
	 */
	public Color getColor();

	/**
	 * Get the thickness of this bullet
	 * @return the thickness of this bullet
	 */
	public float getThickness();
}
