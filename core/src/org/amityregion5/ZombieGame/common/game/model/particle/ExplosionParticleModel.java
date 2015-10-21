package org.amityregion5.ZombieGame.common.game.model.particle;

import org.amityregion5.ZombieGame.client.game.ExplosionParticleDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IParticle;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import box2dLight.Light;

public class ExplosionParticleModel implements IParticle{
	private Light				light;
	private Game				g;
	private Color				c;
	private float x, y, xVel, yVel, rotation, rotationSpeed;

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
	public IDrawingLayer[] getDrawingLayers() {
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
}
