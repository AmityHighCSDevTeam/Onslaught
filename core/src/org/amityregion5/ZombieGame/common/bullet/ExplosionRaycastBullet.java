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
 * A bullet for explosions
 * @author savelyevse17
 */
public class ExplosionRaycastBullet implements IBullet {

	private float			dir, damage; //Direction and damage
	private Game			g; //The game
	private Vector2			endPoint; //The end point
	private Vector2			start; //The start point
	private PlayerModel		source; //The source player
	private List<HitData>	hits; //The hits caused by the raycast

	public ExplosionRaycastBullet(Game g, Vector2 start, float damage, Vector2 bullVector, PlayerModel source) {
		//Set values
		this.g = g;
		this.start = start;
		//Get damage with buffs
		this.damage = (float) ((damage + source.getTotalBuffs().getAdd("explodeDamage")) * source.getTotalBuffs().getMult("explodeDamage"));
		this.source = source;
		endPoint = start.cpy().add(bullVector);
		hits = new ArrayList<HitData>();
	}

	@Override
	public void setDamage(float damage) {
		this.damage = damage;
	}

	@Override
	public void setKnockback(float speed) {}

	@Override
	public float getDamage() {
		return damage;
	}

	@Override
	public float getKnockback() {
		return 0;
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
		return Color.RED;
	}

	@Override
	public Vector2 getEnd() {
		return endPoint;
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

		HitData hitData = new HitData();
		hitData.hit = fixture.getBody();
		hitData.hitPoint = point.cpy(); //Create a hit data and add it to the list
		hitData.dist = start.dst2(point);
		hits.add(hitData);

		//Continue the raycast
		return 1;
	}

	@Override
	public void finishRaycast() {
		//Sort the hits method by distance
		Collections.sort(hits);

		//Looping through the hits closest to farthest
		for (HitData hd : hits) {
			//Apply knockback
			hd.hit.applyLinearImpulse(VectorFactory.createVector(damage / (start.dst(hd.hitPoint) * 100f), dir), hd.hitPoint, true);
			//Get entity
			Optional<IEntityModel<?>> entity = g.getEntityFromBody(hd.hit);

			//Get damage to deal to the entity
			float damageToDeal = damage / start.dst(hd.hitPoint);
			//Get the amount that we can deal
			damageToDeal = Math.min(damageToDeal, damage);

			//If the entity is present and there is damage to deal deal some damage
			if (entity.isPresent() && damageToDeal > 0) {
				damage -= entity.get().damage(damageToDeal, source, DamageTypes.EXPLOSION);
			}

			//If we are out of damage or the distance has gone to far finish
			if (damage <= 0 || damageToDeal <= 0) {
				endPoint = hd.hitPoint;
				break;
			}
		}
	}

	@Override
	public float getThickness() {
		return 1;
	}

	private class HitData implements Comparable<HitData> {
		public double	dist;
		public Body		hit;
		public Vector2	hitPoint;

		@Override
		public int compareTo(HitData o) {
			return Double.compare(dist, o.dist);
		}
	}

	@Override
	public boolean doDraw() {
		return false;
	}
}
