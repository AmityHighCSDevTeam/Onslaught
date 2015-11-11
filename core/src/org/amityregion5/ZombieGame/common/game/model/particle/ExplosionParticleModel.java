package org.amityregion5.ZombieGame.common.game.model.particle;

import org.amityregion5.ZombieGame.client.game.ExplosionParticleDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.json.simple.JSONObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import box2dLight.Light;
import box2dLight.PointLight;

public class ExplosionParticleModel implements IParticle{
	private Light				light;
	private Game				g;
	private Color				c;
	private float x, y, xVel, yVel, rotation, rotationSpeed;

	public ExplosionParticleModel() {
	}
	
	/**
	 * @param x
	 * @param y
	 * @param c
	 * @param g
	 * @param rotation
	 * @param rotationSpeed
	 * @param vel
	 * @param velDir
	 */
	public ExplosionParticleModel(float x, float y, Color c, Game g, float rotation, float rotationSpeed, float vel,
			float velDir) {
		this.x = x;
		this.y = y;
		this.c = c;
		this.g = g;
		this.rotation = rotation;
		this.rotationSpeed = rotationSpeed;
		
		Vector2 vec = VectorFactory.createVector(vel, velDir);
		xVel = vec.x;
		yVel = vec.y;
	}
	public ExplosionParticleModel(float x, float y, Color c, Game g, float rotation, float rotationSpeed, float xV,
			float yV, boolean secondThing) {
		this.x = x;
		this.y = y;
		this.c = c;
		this.g = g;
		this.rotation = rotation;
		this.rotationSpeed = rotationSpeed;
		
		xVel = xV;
		yVel = yV;
	}

	@Override
	public void tick(float timeStep) {
		rotation += rotationSpeed;
		rotationSpeed *= 0.95;
		x += xVel;
		y += yVel;
		if (light != null) {
			light.setActive(true);
			//if (light.getColor().g > 0.1) {
			//	light.setColor(light.getColor().mul(r, g, b, a));
			//}
			light.setColor(light.getColor().mul(0.9f, 0.75f, 0.0f, 0.95f));
			light.setPosition(x, y);
			//light.attachToBody(entity.getBody());
			if (light.getColor().r < 0.05) {
				g.removeParticle(this);
				if (light != null) {
					light.remove();
					light = null;
				}
			}
		}
		//light.setPosition(entity.getBody().getWorldCenter());
		//sprite.getSprite().setOriginCenter();
	}

	@Override
	public void dispose() {
		if (light != null) {
			light.dispose();
			light = null;
		}
	}

	@Override
	public IDrawingLayer[] getFrontDrawingLayers() {
		return new IDrawingLayer[]{ExplosionParticleDrawingLayer.instance};//new IDrawingLayer[] {sprite};
	}

	public void setLight(Light light) {
		this.light = light;
	}

	public Color getColor() {
		return c;
	}

	public Light getLight() {
		return light;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject convertToJSONObject() {
		JSONObject obj = new JSONObject();
		
		obj.put("c", c.toString());
		obj.put("x", x);
		obj.put("y", y);
		obj.put("xv", xVel);
		obj.put("yv", yVel);
		obj.put("r", rotation);
		obj.put("rv", rotationSpeed);
		
		return obj;
	}

	@Override
	public IParticle fromJSON(JSONObject obj, Game g) {
		Color c = Color.valueOf((String) obj.get("c"));
		float x = ((Number)obj.get("x")).floatValue();
		float y = ((Number)obj.get("y")).floatValue();
		float xV = ((Number)obj.get("xv")).floatValue();
		float yV = ((Number)obj.get("yv")).floatValue();
		float r = ((Number)obj.get("r")).floatValue();
		float rV = ((Number)obj.get("rv")).floatValue();
		
		ExplosionParticleModel model = new ExplosionParticleModel(x, y, c, g, r, rV, xV, yV);

		g.addParticleToWorld(model);
		
		model.setLight(new PointLight(g.getLighting(), 50, c, 2, x, y));
		model.getLight().setXray(true);
		
		return model;
	}

	@Override
	public IDrawingLayer[] getBackDrawingLayers() {
		return new IDrawingLayer[]{};
	}
}
