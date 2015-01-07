/**
 * 
 */
package org.amityregion5.ZombieGame.common.entity;

import org.amityregion5.ZombieGame.common.game.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;
import com.sun.javafx.geom.Vec2d;

/**
 * @author savelyevse17
 *
 */
public class EntityBulletTEST implements IEntity, Disposable {

	private Body body;
	private float speed, friction;
	private Game g;

	public EntityBulletTEST(Game g) {
		this.g = g;
	}

	@Override
	public void setShape(Shape e) {}

	@Override
	public Shape getShape() {	
		CircleShape shape = new CircleShape();
		shape.setRadius(0.1f);
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
		if (!getBody().isAwake()) {
			g.removeEntity(this);
		}
	}
}
