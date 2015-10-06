package org.amityregion5.ZombieGame.common.game.model;

import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityGrenade;
import org.amityregion5.ZombieGame.common.game.Game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class GrenadeModel implements IEntityModel<EntityGrenade>{
	private EntityGrenade entity;
	private Game				g;
	private float timeUntilExplosion;
	private double strength;
	private PlayerModel parent;
	private SpriteDrawingLayer sprite;
	private Vector2 explosionPos;

	public GrenadeModel(EntityGrenade e, Game game, PlayerModel parent, String txtr) {
		entity = e;
		g = game;
		this.parent = parent;
		sprite = new SpriteDrawingLayer(new Sprite(TextureRegistry.getTexturesFor(txtr).get(0)));
	}

	@Override
	public EntityGrenade getEntity() {
		return entity;
	}

	@Override
	public void tick(float timeStep) {
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
}
