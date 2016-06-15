package org.amityregion5.onslaught.common.game.model;

import org.amityregion5.onslaught.client.game.IDrawingLayer;
import org.amityregion5.onslaught.common.func.Consumer3;
import org.amityregion5.onslaught.common.game.Game;
import org.json.simple.JSONObject;

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

	/**
	 * Convert to JSON
	 * @return a JSON representation
	 */
	JSONObject convertToJSONObject();

	/**
	 * Get a particle from JSON
	 * 
	 * @param obj the JSON
	 * @param g the game
	 * @return the particle
	 */
	IParticle fromJSON(JSONObject obj, Game g, Consumer3<String, String, Boolean> addErrorConsumer);
	
	Rectangle getRect();
	
	float getRotation();
}
