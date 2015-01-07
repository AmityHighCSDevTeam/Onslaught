/**
 * 
 */
package org.amityregion5.ZombieGame.common.entity;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;

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
public class EntityZombie implements IEntity, Disposable {
	
	private Body body;
	private float speed, friction;
	private IEntity target;
	private Game g;
	
	public EntityZombie(Game g) {
		this.g = g;
		this.speed = 3;
	}

	@Override
	public void setShape(Shape e) {}

	@Override
	public Shape getShape() {	
		CircleShape shape = new CircleShape();
		shape.setRadius(1f);
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
		if (target == null) {
			IEntity closest = null;
			float dist2 = Float.MAX_VALUE;
			for (IEntity e : g.getEntities()) {
				if (e instanceof EntityPlayer) {
					float d = body.getLocalCenter().dst2(e.getBody().getLocalCenter());
					if (d < dist2) {
						closest = e;
						dist2 = d;
					}
				}
			}
			target = closest;
		} else {		
			body.applyForceToCenter(VectorFactory.createVector(getSpeed(), (float) MathHelper.getDirBetweenPoints(body.getPosition(), target.getBody().getPosition())), true);
		}
	}
}
