package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.IEntity;

public interface IEntityModel<T extends IEntity> {
	T getEntity();

	void tick(float timeStep);
	
	void dispose();

	float damage(float damage, IEntityModel<?> source);

	IDrawingLayer[] getDrawingLayers();

	float getHealth();

	float getMaxHealth();

	boolean isHostile();
}
