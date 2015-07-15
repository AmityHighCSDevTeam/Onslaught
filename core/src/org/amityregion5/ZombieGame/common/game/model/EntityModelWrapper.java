package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.IEntity;

public class EntityModelWrapper<T extends IEntity> implements IEntityModel<T> {

	private T entity;
	
	public EntityModelWrapper(T entity) {
		this.entity = entity;
	}
	
	@Override
	public T getEntity() {
		return entity;
	}

	@Override
	public void tick(float timeStep) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void damage(float damage, IEntityModel<?> source) {
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[]{};
	}

	@Override
	public float getHealth() {
		return 0;
	}

	@Override
	public float getMaxHealth() {
		return 0;
	}

	@Override
	public boolean isHostile() {
		return false;
	}
}
