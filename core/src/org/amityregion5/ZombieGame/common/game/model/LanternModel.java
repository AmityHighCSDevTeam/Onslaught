package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityLantern;
import org.amityregion5.ZombieGame.common.game.Game;

import box2dLight.Light;

import com.badlogic.gdx.graphics.Color;

public class LanternModel implements IEntityModel<EntityLantern>{
	public static final Color	LIGHT_COLOR	= new Color(1,1,1,130f/255);

	private Light				light;
	private EntityLantern entity;
	private Game				g;

	public LanternModel(EntityLantern e, Game game) {
		entity = e;
		g = game;
	}

	@Override
	public EntityLantern getEntity() {
		return entity;
	}

	@Override
	public void tick(float timeStep) {
		light.setActive(true);
		light.setPosition(entity.getBody().getWorldCenter());
	}

	@Override
	public void dispose() {
		light.dispose();
		entity.dispose();
	}

	@Override
	public float damage(float damage, IEntityModel<?> source) {
		g.removeEntity(this);
		light.remove();
		return damage;
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[] {};
	}

	@Override
	public float getHealth() {
		return 0;
	}

	@Override
	public float getMaxHealth() {
		return 0;
	}

	public void setLight(Light light) {
		this.light = light;
	}

	@Override
	public boolean isHostile() {
		return false;
	}
	
	public static Color getLIGHT_COLOR() {
		return LIGHT_COLOR;
	}
	
	public Light getLight() {
		return light;
	}
}
