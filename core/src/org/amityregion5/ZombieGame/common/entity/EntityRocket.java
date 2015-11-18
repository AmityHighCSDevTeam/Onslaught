/**
 *
 */
package org.amityregion5.ZombieGame.common.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author savelyevse17
 */
public class EntityRocket implements IEntity, Disposable {

	private Body		body;
	private float		friction;
	private MassData	massData;
	private Vector2[]	shapeDef;

	public EntityRocket(float size) {
		massData = new MassData();
		friction = 0.975f;
		shapeDef = new Vector2[] {new Vector2(size * 2, 0f), new Vector2(0f, size / 2),
				new Vector2(-size * 2, size / 2), new Vector2(-size * 2, -size / 2), new Vector2(0f, -size / 2)};
	}

	@Override
	public void setShape(Shape e) {}

	@Override
	public Shape getShape() {
		PolygonShape poly = new PolygonShape();
		poly.set(shapeDef);
		return poly;
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
}
