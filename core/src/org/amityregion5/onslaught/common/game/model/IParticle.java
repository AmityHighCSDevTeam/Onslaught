package org.amityregion5.onslaught.common.game.model;

import org.amityregion5.onslaught.client.game.IDrawingLayer;
import org.amityregion5.onslaught.common.game.Game;

import com.badlogic.gdx.math.Rectangle;

/**
 * The interface for the particle
 * 
 * @author sergeys
 */
public interface IParticle {
	/**
	 * Tick this particle to update it 
	 * @param timeStep the delta time since the last tick
	 */
	void tick(float timeStep);

	/**
	 * Get rid of data
	 */
	void dispose();

	/**
	 * Get the back drawing layer (before entites)
	 * @return the back drawing layers
	 */
	IDrawingLayer[] getBackDrawingLayers();

	/**
	 * Get the front drawing layer (after entities)
	 * @return the front drawing layers
	 */
	IDrawingLayer[] getFrontDrawingLayers();

	/**
	 * Get the front drawing layer (after entities)
	 * @return the front drawing layers
	 */
	default IDrawingLayer[] getPostLightingDrawingLayers() {
		return new IDrawingLayer[] {};
	}

	/**
	 * Get the maximum drawing layers (after gui)
	 * @return the maximum drawing layers
	 */
	default IDrawingLayer[] getMaxDrawingLayers() {
		return new IDrawingLayer[] {};
	}

	void doPostDeserialize(Game game);
	
	Rectangle getRect();
	
	float getRotation();

	boolean shouldBeDeleted();
}
