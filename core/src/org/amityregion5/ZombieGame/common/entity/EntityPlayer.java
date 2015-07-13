/**
 *
 */
package org.amityregion5.ZombieGame.common.entity;

import java.util.Optional;

import org.amityregion5.ZombieGame.client.game.TextureRegistry;
import org.amityregion5.ZombieGame.common.game.PlayerModel;

import box2dLight.Light;

import com.badlogic.gdx.graphics.g2d.Sprite;
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

	private Body				body;
	private float				speed, friction;
	private MassData			massData;
	private Light				light, circleLight;
	private Sprite				sprite;


	public EntityPlayer() {
		massData = new MassData();

		sprite = new Sprite(TextureRegistry.getTexturesFor("*/Players/**.png").get(0));
	}

	@Override
	public void setShape(Shape e) {
	}

	@Override
	public Shape getShape() {
		CircleShape shape = new CircleShape();
		shape.setRadius(0.15f);
		return shape;
	}

	@Override
	public void dispose() {
		light.remove();
		circleLight.remove();
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
		sprite.setOriginCenter();
	}

	public void setMass(float mass) {
		massData.mass = mass;
	}

	@Override
	public MassData getMassData() {
		return massData;
	}

	@Override
	public void damage(float damage, PlayerModel source) {
	}

	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
	}
	
	public void setCircleLight(Light circleLight) {
		this.circleLight = circleLight;
	}
	
	public Light getCircleLight() {
		return circleLight;
	}

	@Override
	public Optional<Sprite> getSprite() {
		return Optional.ofNullable(sprite);
	}
}
