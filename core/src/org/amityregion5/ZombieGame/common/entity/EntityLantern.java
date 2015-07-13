/**
 *
 */
package org.amityregion5.ZombieGame.common.entity;

import java.util.Optional;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.PlayerModel;

import box2dLight.Light;

import com.badlogic.gdx.graphics.Color;
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
public class EntityLantern implements IEntity, Disposable {

	public static final Color	LIGHT_COLOR	= new Color(1, 1, 1, 0.9f);
	private Body				body;
	private float				friction;
	private Game				g;
	private MassData			massData;
	private Light				light;

	public EntityLantern(Game g) {
		this.g = g;
		massData = new MassData();
	}

	@Override
	public void setShape(Shape e) {
	}

	@Override
	public Shape getShape() {
		CircleShape shape = new CircleShape();
		shape.setRadius(0.05f);
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
		return 0;
	}

	@Override
	public void setSpeed(float f) {
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
		light.setActive(true);
		light.setPosition(getBody().getWorldCenter());
	}

	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
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
		g.removeEntity(this);
		light.remove();
	}

	@Override
	public Optional<Sprite> getSprite() {
		return Optional.ofNullable(null);
	}
}
