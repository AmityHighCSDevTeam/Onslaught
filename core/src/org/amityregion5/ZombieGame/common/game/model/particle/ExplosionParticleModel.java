package org.amityregion5.ZombieGame.common.game.model.particle;

import org.amityregion5.ZombieGame.client.game.ExplosionParticleDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.func.Consumer3;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.json.simple.JSONObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import box2dLight.Light;

public class ExplosionParticleModel implements IParticle {
	//private Light	light; //Light
	private Game	g; //Game
	private Color	c; //Color
	//X, Y, x velocity, y velocity, rotation, rotation speed
	private float	x, y, xVel, yVel, rotation, rotationSpeed;
	private double size = 1.0;

	public ExplosionParticleModel() {}

	/**
	 * 
	 * @param x x position
	 * @param y y position
	 * @param c color
	 * @param g game
	 * @param rotation rotation
	 * @param rotationSpeed rotation speed
	 * @param vel velocity magnitude
	 * @param velDir velocity direction
	 */
	public ExplosionParticleModel(float x, float y, Color c, Game g, float rotation, float rotationSpeed, float vel, float velDir) {
		this.x = x; //Set x
 		this.y = y; //Set y
		this.c = c; //Set color
		this.g = g; //Set game
		this.rotation = rotation; //Set rotation
		this.rotationSpeed = rotationSpeed; //Set rotation speed

		//Calculate x and y components of velocity
		Vector2 vec = VectorFactory.createVector(vel, velDir);
		xVel = vec.x;
		yVel = vec.y;
	}

	/**
	 * 
	 * @param x x position
	 * @param y y position
	 * @param c color
	 * @param g game
	 * @param rotation rotation
	 * @param rotationSpeed rotation speed
	 * @param xV x velocity
	 * @param yV y velocity
	 * @param secondThing unused
	 */
	public ExplosionParticleModel(float x, float y, Color c, Game g, float rotation, float rotationSpeed, float xV, float yV, boolean secondThing) {
		this.x = x;
		this.y = y;
		this.c = c;
		//this.g = g;
		this.rotation = rotation;
		this.rotationSpeed = rotationSpeed;

		xVel = xV;
		yVel = yV;
	}

	@Override
	public void tick(float timeStep) {
		rotation += rotationSpeed; //Rotate
		rotationSpeed *= 0.95; //Slow rotation speed
		x += xVel; //Move x
		y += yVel; //Move y
		
		size *= 0.9;
		
		if (size < 0.05) {
			g.removeParticle(this);
		}
		
		//DO light stuffs
		//if (light != null) {
			//light.dispose();
			//Set active
			//light.setActive(true);

			//Set color
			//light.setColor(light.getColor().mul(0.9f, 0.75f, 0.0f, 0.95f));
			//Set position
			//light.setPosition(x, y);

			//If color dim remove particle
		//	if (light.getColor().r < 0.05) {
		//		g.removeParticle(this);
		//		if (light != null) {
		//			light.remove();
		//			light = null;
		//		}
		//	}
		//}
	}

	@Override
	public void dispose() {
		//if (light != null) {
		//	light.dispose();
		//	light = null;
		//}
	}

	@Override
	public IDrawingLayer[] getPostLightingDrawingLayers() {
		return new IDrawingLayer[] {ExplosionParticleDrawingLayer.instance};// new IDrawingLayer[] {sprite};
	}

	public void setLight(Light light) {
		//this.light = light;
	}

	public Color getColor() {
		return c.cpy().mul(0.9f, 0.75f, 0.0f, 0.95f).mul((float)size);
	}

	//public Light getLight() {
		//return light;
	//}

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
		obj.put("size", size);

		return obj;
	}

	@Override
	public IParticle fromJSON(JSONObject obj, Game g, Consumer3<String, String, Boolean> addErrorConsumer) {
		Color c = Color.valueOf((String) obj.get("c"));
		float x = ((Number) obj.get("x")).floatValue();
		float y = ((Number) obj.get("y")).floatValue();
		float xV = ((Number) obj.get("xv")).floatValue();
		float yV = ((Number) obj.get("yv")).floatValue();
		float r = ((Number) obj.get("r")).floatValue();
		float rV = ((Number) obj.get("rv")).floatValue();
		double size = ((Number) obj.get("size")).doubleValue();

		ExplosionParticleModel model = new ExplosionParticleModel(x, y, c, g, r, rV, xV, yV);
		
		model.size = size;

		g.addParticleToWorld(model);

		//model.setLight(new PointLight(g.getLighting(), 50, c, 2, x, y));
		//model.getLight().setXray(true);

		return model;
	}

	@Override
	public IDrawingLayer[] getBackDrawingLayers() {
		return new IDrawingLayer[] {};
	}

	public double getSize() {
		return size;
	}

	@Override
	public IDrawingLayer[] getFrontDrawingLayers() {
		return new IDrawingLayer[] {};
	}
	
	@Override
	public Rectangle getRect() {
		return new Rectangle((float)(getX() - (0.15 / Math.sqrt(getSize()))), (float)(getY() - (0.15 / Math.sqrt(getSize()))), (float)(0.15 / Math.sqrt(getSize()) * 2), (float)(0.15 / Math.sqrt(getSize()) * 2));
	}
}
