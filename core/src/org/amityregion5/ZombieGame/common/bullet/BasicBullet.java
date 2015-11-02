/**
 *
 */
package org.amityregion5.ZombieGame.common.bullet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.amityregion5.ZombieGame.common.game.DamageTypes;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
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

	private float	knockback, dir, damage, range;
	private Game	g;
	private Vector2	endPoint;
	private Vector2	start;
	private PlayerModel source;
	private Color color;
	private float bulletThickness;
	private List<HitData> hits;

	public BasicBullet(Game g, Vector2 start, float speed, float damage,
			Vector2 bullVector, PlayerModel source, Color color, float bulletThickness, float range) {
		this.g = g;
		this.start = start;
		knockback = speed;
		this.damage = (float) ((damage+source.getTotalBuffs().getAdd("bulletDamage"))*source.getTotalBuffs().getMult("bulletDamage"));
		this.source = source;
		this.color = color;
		this.range = range;
		this.bulletThickness = bulletThickness;
		endPoint = start.cpy().add(bullVector);
		hits = new ArrayList<HitData>();
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
		return endPoint;
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {

		HitData hitData = new HitData();
		hitData.hit = fixture.getBody();
		hitData.hitPoint = point.cpy();
		hitData.dist = start.dst2(point);

		if (start.dst2(point) <= range*range) {
			hits.add(hitData);
		}

		return 1;
	}

	@Override
	public void finishRaycast() {
		Collections.sort(hits);

		for (HitData hd : hits) {
			hd.hit.applyLinearImpulse(VectorFactory.createVector(knockback, dir), hd.hitPoint, true);
			Optional<IEntityModel<?>> entity = g.getEntityFromBody(hd.hit);

			if (entity.isPresent()) {
				damage -= entity.get().damage(damage, source, DamageTypes.BULLET);
			}

			if (damage <= 0) {
				endPoint = hd.hitPoint;
				break;
			}
		}
	}

	@Override
	public float getThickness() {
		return bulletThickness;
	}

	private class HitData implements Comparable<HitData> {
		public double dist;
		public Body hit;
		public Vector2 hitPoint;

		@Override
		public int compareTo(HitData o) {
			return Double.compare(dist, o.dist);
		}
	}

	@Override
	public boolean doDraw() {
		return true;
	}
}
