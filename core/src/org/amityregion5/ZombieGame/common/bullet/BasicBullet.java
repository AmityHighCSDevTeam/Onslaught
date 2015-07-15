/**
 *
 */
package org.amityregion5.ZombieGame.common.bullet;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.PlayerModel;
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

	private float	knockback, dir, damage;
	private Game	g;
	private Body	hit;
	private Vector2	hitPoint;
	private Vector2	start;
	private PlayerModel source;
	private Color color;
	private float bulletThickness;

	public BasicBullet(Game g, Vector2 start, float speed, float damage,
			Vector2 bullVector, PlayerModel source, Color color, float bulletThickness) {
		this.g = g;
		this.start = start;
		knockback = speed;
		this.damage = damage;
		this.source = source;
		this.color = color;
		this.bulletThickness = bulletThickness;
		hitPoint = start.cpy().add(bullVector);
	}

	@Override
	public void setDamage(float damage) {
		this.damage = damage;
	}

	@Override
	public void setKnockback(float speed) {
		knockback = speed;
	}

	@Override
	public float getDamage() {
		return damage;
	}

	@Override
	public float getKnockback() {
		return knockback;
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
		return color;
	}

	@Override
	public Vector2 getEnd() {
		return hitPoint;
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		hit = fixture.getBody();
		hitPoint = point.cpy();

		return fraction;
	}

	@Override
	public void finishRaycast() {
		if (hit != null) {
			hit.applyLinearImpulse(VectorFactory.createVector(knockback, dir),
					hitPoint, true);
		}

		g.getEntityFromBody(hit).ifPresent(e -> e.damage(damage, source));
		;
	}
	
	@Override
	public float getThickness() {
		return bulletThickness;
	}
}
