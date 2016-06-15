/**
 *
 */
package org.amityregion5.onslaught.common.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author savelyevse17
 */
public class EntityZombie implements IEntity, Disposable {

	private Body		body; //The body
	private float		friction; //The friction
	private MassData	massData; //The mass
	private float		size; //The size

	public EntityZombie(float size) {
		massData = new MassData();
		this.size = size;
	}

	@Override
	public void setShape(Shape e) {}

	@Override
	public Shape getShape() {
		CircleShape shape = new CircleShape();
		shape.setRadius(size);
		return shape;
	}

	@Override
	public void dispose() {}

	@Override
	public void setBody(Body b) {
		body = b;
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public float getFriction() {
		return friction;
	}

	@Override
	public void setFriction(float f) {
		friction = f;
	}

	public void setMass(float mass) {
		massData.mass = mass;
	}

	@Override
	public MassData getMassData() {
		return massData;
	}

	public float getSize() {
		return size;
	}
}
