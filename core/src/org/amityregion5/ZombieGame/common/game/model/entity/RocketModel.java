package org.amityregion5.ZombieGame.common.game.model.entity;

import java.util.HashMap;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityRocket;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.particle.ExplosionParticleModel;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.util.MapUtil;
import org.amityregion5.ZombieGame.common.weapon.data.SoundData;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * A Model to represent Rockets
 * 
 * @author sergeys
 *
 */
public class RocketModel implements IEntityModel<EntityRocket> {
	private static final float			timeStepPerSmoke	= 0.05f; //The seconds between smoke particles
	
	private HashMap<String, JsonElement> data = new HashMap<String, JsonElement>();
	
	//private float				timeUntilExplosion; //Time until it explodes
	//private double				strength; //Strength of the explosion
	//private float				acceleration; //Acceleration of the rocket
	//private float				size; //The size of the rocket
	private float				timeUntilSmoke; //Seconds until next smoke
	
	private transient SoundData			flySound; //The fly sound
	private transient EntityRocket		entity; //The entity
	private transient Game				g; //The game
	private transient PlayerModel			parent; //Player that created the rocket
	private transient SpriteDrawingLayer	sprite; //The drawing layer
	private transient Vector2				explosionPos; //The explosion position

	public RocketModel() {}

	/**
	 * Create a rocket
	 * 
	 * @param e the entity
	 * @param game the game
	 * @param parent the player that created it
	 * @param txtr the texture name
	 * @param size the size of the rocket
	 * @param flySound the sound to emit while it flys
	 */
	public RocketModel(EntityRocket e, Game game, PlayerModel parent, String txtr, float size, SoundData flySound) {
		entity = e;
		g = game;
		this.parent = parent; //Set variables
		sprite = new SpriteDrawingLayer(txtr, this::getSizeM2);
		timeUntilSmoke = timeStepPerSmoke;
		this.flySound = flySound;
	}

	@Override
	public EntityRocket getEntity() {
		return entity;
	}

	@Override
	public void tick(float timeStep) {
		//While there is time until it explodes
		if (getTimeUntilExplosion() > 0) {
			//Play the fly sound if it exists
			if (flySound != null) {
				g.playSound(flySound, entity.getBody().getWorldCenter());
			}
			
			//Count down time until explosion and time until smoke
			setTimeUntilExplosion(getTimeUntilExplosion() - timeStep);
			timeUntilSmoke -= timeStep;
			
			//Set explosion position to current position
			explosionPos = entity.getBody().getWorldCenter().cpy();
			//Apply acceleration force
			entity.getBody().applyForceToCenter(VectorFactory.createVector(getAcceleration(), entity.getBody().getAngle()), true);
			//If it is time to smoke
			if (timeUntilSmoke < 0) {
				//Increase the time until the next smoke
				timeUntilSmoke += timeStepPerSmoke;

				//Get position for the smoke relative to the rocket
				Vector2 pos2 = VectorFactory.createVector(getSize() * 2 + Math.min(0.05f, getSize() * 0.1f), entity.getBody().getAngle() + (float) Math.PI);

				//Get the world position
				pos2 = pos2.add(entity.getBody().getWorldCenter());

				//Create a smoke particle
				ExplosionParticleModel smoke = new ExplosionParticleModel(pos2.x, pos2.y, new Color(1f, 1f, 0f, 1f), g,
						(float) (2 * Math.PI * g.getRandom().nextDouble()), 100 * (g.getRandom().nextFloat() - 0.5f), 0.05f,
						entity.getBody().getAngle() + (float) Math.PI);

				//Set light
				//smoke.setLight(new PointLight(g.getLighting(), 10, smoke.getColor(), 2, pos2.x, pos2.y));
				//smoke.getLight().setXray(true); //Turn xray on (makes computations easier)

				//Add the particle to the world
				g.addParticleToWorld(smoke);
			}
		} else {
			//If time to explode
			//Remove the entity
			g.removeEntity(this);
			//Make the explosion
			g.makeExplosion(explosionPos, getStrength(), parent);
		}
		// light.setPosition(entity.getBody().getWorldCenter());
		sprite.getSprite().setOriginCenter();
	}

	@Override
	public void dispose() {
		parent = null;
		entity.dispose();
	}

	@Override
	public float damage(float damage, IEntityModel<?> source, String damageType) {
		setTimeUntilExplosion(0);
		explosionPos = entity.getBody().getWorldCenter().cpy(); //Set explosion position
		//Will now explode next tick
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

	public void onHit() {
		//When this rocket impacts another body damage it so it will explode next tick
		damage(0, this, "--Rocket Hit Target Damage--");
	}

	/**
	 * Set the acceleration
	 * 
	 * @param acceleration the acceleration
	 */
	public void setAcceleration(float acceleration) {
		data.put("acceleration", new JsonPrimitive(acceleration));
	}

	/**
	 * Get the acceleration
	 * 
	 * @return the acceleration
	 */
	public float getAcceleration() {
		return data.get("acceleration").getAsFloat();
	}

	/**
	 * Get the size
	 * 
	 * @return the size
	 */
	public float getSize() {
		return entity.getSize();
	}

	/**
	 * Get the size times 2
	 * 
	 * @return the size times 2
	 */
	public float getSizeM2() {
		return getSize() * 2;
	}
	
	@Override
	public void read(JsonObject obj) {
		data = MapUtil.convertToHashMap(obj.entrySet());
	}

	@Override
	public void doPostDeserialize(Game game) {
		entity = new EntityRocket(data.get("size").getAsFloat());
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
		data.remove("txtr");
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
		obj.addProperty("txtr", sprite.getTxtrName());
	}
}
