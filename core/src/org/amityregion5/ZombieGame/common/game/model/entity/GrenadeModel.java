package org.amityregion5.ZombieGame.common.game.model.entity;

import java.util.HashMap;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityGrenade;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.util.MapUtil;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * A model representing a grenade
 * 
 * @author sergeys
 *
 */
public class GrenadeModel implements IEntityModel<EntityGrenade> {
	private HashMap<String, JsonElement> data = new HashMap<String, JsonElement>();

	private transient EntityGrenade		entity; //The entity
	private transient Game				g; //The game
	private transient PlayerModel			parent; //The grenade's thrower
	private transient SpriteDrawingLayer	sprite; //The sprite
	private transient Vector2				explosionPos; //The explosion position

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
		if (getTimeUntilExplosion() > 0) {
			setTimeUntilExplosion(getTimeUntilExplosion()-timeStep); //Tick down time until explosion
			explosionPos = entity.getBody().getWorldCenter().cpy();
		} else {
			g.removeEntity(this); //Explode
			g.makeExplosion(explosionPos, getStrength(), parent);
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
		setTimeUntilExplosion(0);
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
		return data.get("timeUntilExplosion").getAsFloat();
	}

	/**
	 * @param timeUntilExplosion
	 *            the timeUntilExplosion to set
	 */
	public void setTimeUntilExplosion(float timeUntilExplosion) {
		data.put("timeUntilExplosion", new JsonPrimitive(timeUntilExplosion));
	}

	/**
	 * @return the strength
	 */
	public double getStrength() {
		return data.get("strength").getAsDouble();
	}

	/**
	 * @param strength
	 *            the strength to set
	 */
	public void setStrength(double strength) {
		data.put("strength", new JsonPrimitive(strength));
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
	
	@Override
	public void read(JsonObject obj) {
		data = MapUtil.convertToHashMap(obj.entrySet());
	}

	@Override
	public void doPostDeserialize(Game game) {
		entity = new EntityGrenade(data.get("size").getAsFloat());
		g = game;
		parent = g.getSingleplayerPlayer(); //TODO: Save Player
		sprite = new SpriteDrawingLayer(data.get("txtr").getAsString());
		entity.setFriction(data.get("friction").getAsFloat());
		entity.setMass(data.get("mass").getAsFloat());

		game.addEntityToWorld(this, data.get("x").getAsFloat(), data.get("y").getAsFloat());
		entity.getBody().getTransform().setPosition(new Vector2(data.get("x").getAsFloat(), data.get("y").getAsFloat()));
		entity.getBody().getTransform().setRotation(data.get("r").getAsFloat());
		entity.getBody().setLinearVelocity(data.get("vx").getAsFloat(), data.get("vy").getAsFloat());

		data.remove("size");
		data.remove("friction");
		data.remove("mass");
		data.remove("x");
		data.remove("y");
		data.remove("r");
		data.remove("vx");
		data.remove("vy");
	}
	
	@Override
	public void write(JsonObject obj) {
		MapUtil.addMapToJson(obj, data);
		obj.addProperty("size", entity.getSize());
		obj.addProperty("friction", entity.getFriction());
		obj.addProperty("mass", entity.getMassData().mass);
		obj.addProperty("x", entity.getBody().getWorldCenter().x);
		obj.addProperty("y", entity.getBody().getWorldCenter().y);
		obj.addProperty("r", entity.getBody().getTransform().getRotation());
		obj.addProperty("vx", entity.getBody().getLinearVelocity().x);
		obj.addProperty("vy", entity.getBody().getLinearVelocity().y);
	}
}
