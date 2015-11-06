package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.IEntity;
import org.amityregion5.ZombieGame.common.game.Game;
import org.json.simple.JSONObject;

/**
 * 
 * @author sergeys
 *
 * @param <T>
 */
public interface IEntityModel<T extends IEntity> {
	T getEntity();

	void tick(float timeStep);
	
	void dispose();

	IDrawingLayer[] getDrawingLayers();

	float damage(float damage, IEntityModel<?> source, String damageType);

	float getHealth();

	float getMaxHealth();

	boolean isHostile();
	
	JSONObject convertToJSONObject();
	
	IEntityModel<T> fromJSON(JSONObject obj, Game g);
}
