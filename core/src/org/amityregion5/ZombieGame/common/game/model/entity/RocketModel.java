package org.amityregion5.ZombieGame.common.game.model.entity;

import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.common.entity.EntityRocket;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.particle.ExplosionParticleModel;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.weapon.data.SoundData;
import org.json.simple.JSONObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import box2dLight.PointLight;

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
	private final float timeStepPerSmoke = 0.05f;
	private float timeUntilSmoke;
	private SoundData flySound;
	
	public RocketModel() {
	}

	public RocketModel(EntityRocket e, Game game, PlayerModel parent, String txtr, float size, SoundData flySound) {
		entity = e;
		g = game;
		this.parent = parent;
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
		if (timeUntilExplosion > 0) {
			if (flySound != null) {
				g.playSound(flySound, entity.getBody().getWorldCenter());
			}
			timeUntilExplosion -= timeStep;
			timeUntilSmoke -= timeStep;
			explosionPos = entity.getBody().getWorldCenter().cpy();
			entity.getBody().applyForceToCenter(VectorFactory.createVector(acceleration,entity.getBody().getAngle()), true);
			if (timeUntilSmoke < 0) {
				timeUntilSmoke += timeStepPerSmoke;

				Vector2 pos2 = VectorFactory.createVector(size*2 + Math.min(0.05f, size*0.1f), entity.getBody().getAngle() + (float)Math.PI);
				
				pos2 = pos2.add(entity.getBody().getWorldCenter());

				ExplosionParticleModel smoke = new ExplosionParticleModel(pos2.x, pos2.y, new Color(1f, 1f, 0f, 1f), g,
						(float) (2*Math.PI*g.getRandom().nextDouble()), 100*(g.getRandom().nextFloat()-0.5f),
						0.05f, entity.getBody().getAngle() + (float)Math.PI);

				smoke.setLight(new PointLight(g.getLighting(), 50, smoke.getColor(), 2, pos2.x, pos2.y));
				smoke.getLight().setXray(true);

				g.addParticleToWorld(smoke);
			}
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
	public float damage(float damage, IEntityModel<?> source, String damageType) {
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
		damage(0, this, "--Rocket Hit Target Damage--");
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
		//TODO: Add player to save list

		return obj;
	}

	@Override
	public RocketModel fromJSON(JSONObject obj, Game g) {
		float x = ((Number)obj.get("x")).floatValue();
		float y = ((Number)obj.get("y")).floatValue();
		float r = ((Number)obj.get("r")).floatValue();
		float t = ((Number)obj.get("t")).floatValue();
		double s = ((Number)obj.get("s")).doubleValue();
		float size = ((Number)obj.get("size")).floatValue();
		float m = ((Number)obj.get("m")).floatValue();
		float f = ((Number)obj.get("f")).floatValue();
		float a = ((Number)obj.get("a")).floatValue();
		SoundData fS = SoundData.getSoundData((JSONObject) obj.get("sound"));
		
		RocketModel model = new RocketModel(new EntityRocket(size), g, null, sprite.getTxtrName(), size, fS);
		model.setTimeUntilExplosion(t);
		model.setStrength(s);
		model.setAcceleration(a);
		g.addEntityToWorld(model, x, y);
		model.getEntity().getBody().getTransform().setPosition(new Vector2(x,y));
		model.getEntity().getBody().getTransform().setRotation(r);
		model.getEntity().setFriction(f);
		model.getEntity().setMass(m);
		
		return model;
	}
}
