package org.amityregion5.ZombieGame.common.game.model.entity;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityRocket;
import org.amityregion5.ZombieGame.common.func.Consumer3;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.particle.ExplosionParticleModel;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.weapon.data.SoundData;
import org.json.simple.JSONObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * A Model to represent Rockets
 * 
 * @author sergeys
 *
 */
public class RocketModel implements IEntityModel<EntityRocket> {
	private EntityRocket		entity; //The entity
	private Game				g; //The game
	private float				timeUntilExplosion; //Time until it explodes
	private double				strength; //Strength of the explosion
	private float				acceleration; //Acceleration of the rocket
	private PlayerModel			parent; //Player that created the rocket
	private SpriteDrawingLayer	sprite; //The drawing layer
	private Vector2				explosionPos; //The explosion position
	private float				size; //The size of the rocket
	private final float			timeStepPerSmoke	= 0.05f; //The seconds between smoke particles
	private float				timeUntilSmoke; //Seconds until next smoke
	private SoundData			flySound; //The fly sound

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
		this.size = size;
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
		if (timeUntilExplosion > 0) {
			//Play the fly sound if it exists
			if (flySound != null) {
				g.playSound(flySound, entity.getBody().getWorldCenter());
			}
			
			//Count down time until explosion and time until smoke
			timeUntilExplosion -= timeStep;
			timeUntilSmoke -= timeStep;
			
			//Set explosion position to current position
			explosionPos = entity.getBody().getWorldCenter().cpy();
			//Apply acceleration force
			entity.getBody().applyForceToCenter(VectorFactory.createVector(acceleration, entity.getBody().getAngle()), true);
			//If it is time to smoke
			if (timeUntilSmoke < 0) {
				//Increase the time until the next smoke
				timeUntilSmoke += timeStepPerSmoke;

				//Get position for the smoke relative to the rocket
				Vector2 pos2 = VectorFactory.createVector(size * 2 + Math.min(0.05f, size * 0.1f), entity.getBody().getAngle() + (float) Math.PI);

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
			g.makeExplosion(explosionPos, strength, parent);
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
		timeUntilExplosion = 0; //If it is damage set time to explosion to zero
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
		this.acceleration = acceleration;
	}

	/**
	 * Get the acceleration
	 * 
	 * @return the acceleration
	 */
	public float getAcceleration() {
		return acceleration;
	}

	/**
	 * Get the size
	 * 
	 * @return the size
	 */
	public float getSize() {
		return size;
	}

	/**
	 * Get the size times 2
	 * 
	 * @return the size times 2
	 */
	public float getSizeM2() {
		return size * 2;
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
		obj.put("size", size);
		obj.put("txtr", sprite.getTxtrName());
		obj.put("m", entity.getMassData().mass);
		obj.put("f", entity.getFriction());
		obj.put("a", acceleration);
		obj.put("sound", flySound.toJSON());
		// TODO: Add player to save list

		return obj;
	}

	@Override
	public void fromJSON(JSONObject obj, Game g, Consumer3<String, String, Boolean> addErrorConsumer) {
		float x = ((Number) obj.get("x")).floatValue();
		float y = ((Number) obj.get("y")).floatValue();
		float r = ((Number) obj.get("r")).floatValue();
		float t = ((Number) obj.get("t")).floatValue();
		double s = ((Number) obj.get("s")).doubleValue();
		float size = ((Number) obj.get("size")).floatValue();
		float m = ((Number) obj.get("m")).floatValue();
		float f = ((Number) obj.get("f")).floatValue();
		float a = ((Number) obj.get("a")).floatValue();
		SoundData fS = SoundData.getSoundData((JSONObject) obj.get("sound"));

		RocketModel model = new RocketModel(new EntityRocket(size), g, null, sprite.getTxtrName(), size, fS);
		model.getEntity().setFriction(f);
		model.getEntity().setMass(m);
		model.setTimeUntilExplosion(t);
		model.setStrength(s);
		model.setAcceleration(a);
		g.addEntityToWorld(model, x, y);
		model.getEntity().getBody().getTransform().setPosition(new Vector2(x, y));
		model.getEntity().getBody().getTransform().setRotation(r);
	}
}
