/**
 * 
 */
package org.amityregion5.ZombieGame.common.bullet;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * @author savelyevse17
 *
 */
public class BasicBullet implements IBullet {

	private float speed, dir, damage, mass;
	private Game g;
	private Body hit;
	private Vector2 hitPoint;
	private Vector2 start;

	public BasicBullet(Game g, Vector2 start, float speed, float mass, float damage, Vector2 bullVector) {
		this.g = g;
		this.start = start;
		this.speed = speed;
		this.damage = damage;
		this.mass = mass;
		this.hitPoint = start.cpy().add(bullVector);
	}

	@Override
	public void setDamage(float damage) {
		this.damage = damage;
	}
	@Override
	public void setMass(float mass) {
		this.mass = mass;
	}
	@Override
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	@Override
	public float getDamage() {
		return damage;
	}
	@Override
	public float getMass() {
		return mass;
	}
	@Override
	public float getSpeed() {
		return speed;
	}
	@Override
	public void setDir(float dir) {
		this.dir = dir;
	}
	@Override
	public float getDir() {
		return dir;
	}
	@Override
	public void setStart(Vector2 start) {
		this.start = start;
	}
	@Override
	public Vector2 getStart() {
		return start;
	}
	@Override
	public Color getColor() {
		return Color.YELLOW;
	}
	@Override
	public Vector2 getEnd() {
		return hitPoint;
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		hit = fixture.getBody();
		hitPoint = point.cpy();
		
		return fraction;
	}

	public void finishRaycast() {
		if (hit != null) {
			hit.applyLinearImpulse(VectorFactory.createVector(speed * mass, dir), hitPoint, true);
		}
		
		g.getEntityFromBody(hit).ifPresent(e -> e.damage(damage));;
	}
}
