/**
 * 
 */
package org.amityregion5.ZombieGame.common.entity;

import box2dLight.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author savelyevse17
 *
 */
public class EntityPlayer implements IEntity, Disposable {

	private Body body;
	private float speed, friction;
	private MassData massData;
	private Light light;

	public EntityPlayer() {
		massData = new MassData();
	}

	@Override
	public void setShape(Shape e) {}

	@Override
	public Shape getShape() {	
		CircleShape shape = new CircleShape();
		shape.setRadius(0.15f);
		return shape;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void setBody(Body b) {
		body = b;
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	@Override
	public void setSpeed(float f) {
		speed = f;
	}

	@Override
	public float getFriction() {
		return friction;
	}

	@Override
	public void setFriction(float f) {
		friction = f;
	}

	@Override
	public void tick(float delta) {
		if (Gdx.input.isKeyPressed(Keys.W)) {
			getBody().applyForceToCenter(new Vector2(0, getSpeed()), true);
		}	
		if (Gdx.input.isKeyPressed(Keys.S)) {
			getBody().applyForceToCenter(new Vector2(0, -getSpeed()), true);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			getBody().applyForceToCenter(new Vector2(getSpeed(),0), true);
		}	
		if (Gdx.input.isKeyPressed(Keys.A)) {
			getBody().applyForceToCenter(new Vector2(-getSpeed(), 0), true);
		}
		light.setDirection((float) Math.toDegrees(getBody().getAngle()));
		light.setPosition(getBody().getWorldCenter());
	}

	public void setMass(float mass) {
		massData.mass = mass;
	}

	@Override
	public MassData getMassData() {
		return massData;
	}

	@Override
	public void damage(float damage) {
		// TODO Auto-generated method stub

	}
	
	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
	}
}
