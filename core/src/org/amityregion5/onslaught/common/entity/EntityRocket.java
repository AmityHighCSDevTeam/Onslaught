/**
 *
 */
package org.amityregion5.onslaught.common.entity;

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

	private Body		body; //The body
	private float		friction; //The friction
	private float size;
	private MassData	massData; //The mass data
	private Vector2[]	shapeDef; //The set of points defining the shape

	public EntityRocket(float size) {
		massData = new MassData();
		this.size = size;
		friction = 0.975f; 
		//Create the shape definition
		shapeDef = new Vector2[] {new Vector2(size * 2, 0f), new Vector2(0f, size / 2), new Vector2(-size * 2, size / 2), new Vector2(-size * 2, -size / 2),
				new Vector2(0f, -size / 2)};
	}
	
	public float getSize() {
		return size;
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
