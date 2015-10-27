package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.common.entity.IEntity;

public interface IEntityModel<T extends IEntity> extends IParticle {
	T getEntity();

	//void tick(float timeStep);
	
	//void dispose();

	float damage(float damage, IEntityModel<?> source, String damageType);

	//IDrawingLayer[] getDrawingLayers();

	float getHealth();

	float getMaxHealth();

	boolean isHostile();
}
