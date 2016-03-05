package org.amityregion5.ZombieGame.common.game.model.entity;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityGrenade;
import org.amityregion5.ZombieGame.common.func.Consumer3;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;

/**
 * A model representing a grenade
 * 
 * @author sergeys
 *
 */
public class GrenadeModel implements IEntityModel<EntityGrenade> {
	private EntityGrenade		entity; //The entity
	private Game				g; //The game
	private float				timeUntilExplosion; //Time until it explodes
	private double				strength; //The strength of the explosion
	private PlayerModel			parent; //The grenade's thrower
	private SpriteDrawingLayer	sprite; //The sprite
	private Vector2				explosionPos; //The explosion position

	public GrenadeModel() {}

	public GrenadeModel(EntityGrenade e, Game game, PlayerModel parent, String txtr) {
		entity = e;
		g = game; //Set values
		this.parent = parent;
		sprite = new SpriteDrawingLayer(txtr);
	}

	@Override
	public EntityGrenade getEntity() {
		return entity;
	}

	@Override
	public void tick(float timeStep) {
		//If time until explosion remains
		if (timeUntilExplosion > 0) {
			timeUntilExplosion -= timeStep; //Tick down time until explosion
			explosionPos = entity.getBody().getWorldCenter().cpy();
		} else {
			g.removeEntity(this); //Explode
			g.makeExplosion(explosionPos, strength, parent);
		}
		//Move sprite to the right place
		sprite.getSprite().setOriginCenter();
	}

	@Override
	public void dispose() {
		parent = null;
		entity.dispose();//clean up
	}

	@Override
	public float damage(float damage, IEntityModel<?> source, String damageType) {
		timeUntilExplosion = 0; //explode if damaged
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
	 * @param timeUntilExplosion
	 *            the timeUntilExplosion to set
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
	 * @param strength
	 *            the strength to set
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
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(PlayerModel parent) {
		this.parent = parent;
	}

	@Override
	public boolean isHostile() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject convertToJSONObject() {
		JSONObject obj = new JSONObject();

		obj.put("x", entity.getBody().getWorldCenter().x);
		obj.put("y", entity.getBody().getWorldCenter().y);
		obj.put("r", entity.getBody().getTransform().getRotation());
		obj.put("t", timeUntilExplosion);
		obj.put("s", strength);
		obj.put("size", entity.getSize());
		obj.put("txtr", sprite.getTxtrName());
		obj.put("m", entity.getMassData().mass);
		obj.put("f", entity.getFriction());
		// TODO: Add player to save list

		return obj;
	}

	@Override
	public IEntityModel<EntityGrenade> fromJSON(JSONObject obj, Game g, Consumer3<String, String, Boolean> addErrorConsumer) {
		float x = ((Number) obj.get("x")).floatValue();
		float y = ((Number) obj.get("y")).floatValue();
		float r = ((Number) obj.get("r")).floatValue();
		float t = ((Number) obj.get("t")).floatValue();
		double s = ((Number) obj.get("s")).doubleValue();
		float size = ((Number) obj.get("size")).floatValue();
		float m = ((Number) obj.get("m")).floatValue();
		float f = ((Number) obj.get("f")).floatValue();

		GrenadeModel model = new GrenadeModel(new EntityGrenade(size), g, null, sprite.getTxtrName());
		model.getEntity().setFriction(f);
		model.getEntity().setMass(m);
		model.setTimeUntilExplosion(t);
		model.setStrength(s);
		g.addEntityToWorld(model, x, y);
		model.getEntity().getBody().getTransform().setPosition(new Vector2(x, y));
		model.getEntity().getBody().getTransform().setRotation(r);

		return model;
	}
}
