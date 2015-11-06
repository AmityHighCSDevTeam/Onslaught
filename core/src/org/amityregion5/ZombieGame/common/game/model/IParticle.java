package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.json.simple.JSONObject;

/**
 * 
 * @author sergeys
 *
 */
public interface IParticle {
	void tick(float timeStep);
	
	void dispose();

	IDrawingLayer[] getDrawingLayers();
	
	JSONObject convertToJSONObject();
	
	IParticle fromJSON(JSONObject obj, Game g);
}
