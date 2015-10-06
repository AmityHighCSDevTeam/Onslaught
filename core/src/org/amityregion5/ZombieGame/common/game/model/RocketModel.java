package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityRocket;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class RocketModel implements IEntityModel<EntityRocket>{
	private EntityRocket entity;
	private Game				g;
	private float timeUntilExplosion;
	private double strength;
	private float acceleration;
	private PlayerModel parent;
	private SpriteDrawingLayer sprite;
	private Vector2 explosionPos;
	private float size;

	public RocketModel(EntityRocket e, Game game, PlayerModel parent, String txtr, float size) {
		entity = e;
		g = game;
		this.parent = parent;
		this.size = size;
		sprite = new SpriteDrawingLayer(new Sprite(TextureRegistry.getTexturesFor(txtr).get(0)), this::getSizeM2);
	}

	@Override
	public EntityRocket getEntity() {
		return entity;
	}

	@Override
	public void tick(float timeStep) {
		entity.getBody().applyForceToCenter(VectorFactory.createVector(acceleration,entity.getBody().getAngle()), true);
		if (timeUntilExplosion > 0) {
			timeUntilExplosion -= timeStep;
			explosionPos = entity.getBody().getWorldCenter().cpy();
		} else {
			g.removeEntity(this);
			g.makeExplosion(explosionPos, strength, parent);
		}
		//light.setPosition(entity.getBody().getWorldCenter());
		sprite.getSprite().setOriginCenter();
	}

	@Override
	public void dispose() {
		parent = null;
		entity.dispose();
	}

	@Override
	public float damage(float damage, IEntityModel<?> source) {
		timeUntilExplosion = 0;
		explosionPos = entity.getBody().getWorldCenter().cpy();
		return 0;
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[] {sprite};
	}

	@Override
	public float getHealth() {
		return 0;
	}

	@Override
	public float getMaxHealth() {
		return 0;
	}

	/**
	 * @return the timeUntilExplosion
	 */
	public float getTimeUntilExplosion() {
		return timeUntilExplosion;
	}

	/**
	 * @param timeUntilExplosion the timeUntilExplosion to set
	 */
	public void setTimeUntilExplosion(float timeUntilExplosion) {
		this.timeUntilExplosion = timeUntilExplosion;
	}

	/**
	 * @return the strength
	 */
	public double getStrength() {
		return strength;
	}

	/**
	 * @param strength the strength to set
	 */
	public void setStrength(double strength) {
		this.strength = strength;
	}

	/**
	 * @return the parent
	 */
	public PlayerModel getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(PlayerModel parent) {
		this.parent = parent;
	}

	@Override
	public boolean isHostile() {
		return false;
	}
	
	public void onHit() {
		damage(0, this);
	}
	
	public void setAcceleration(float acceleration) {
		this.acceleration = acceleration;
	}
	
	public float getAcceleration() {
		return acceleration;
	}
	
	public float getSize() {
		return size;
	}
	
	public float getSizeM2() {
		return size*2;
	}
}