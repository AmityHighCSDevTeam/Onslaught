/**
 * 
 */
package org.amityregion5.ZombieGame.common.entity;

import java.util.Optional;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.game.TextureRegistry;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.BodyHelper;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;

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
public class EntityZombie implements IEntity, Disposable {
	
	private Body body;
	private float speed, friction, health;
	private IEntity target;
	private Game g;
	private MassData massData;
	private int textureIndex = ZombieGame.instance.random.nextInt(TextureRegistry.textures.get("Zombie").size);
	private Sprite zSprite;
	
	public EntityZombie(Game g) {
		this.g = g;
		massData = new MassData();
		zSprite = new Sprite(TextureRegistry.textures.get("Zombie").get(textureIndex));
	}

	@Override
	public void setShape(Shape e) {}

	@Override
	public Shape getShape() {	
		CircleShape shape = new CircleShape();
		shape.setRadius(0.15f);
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
	
	public void setHealth(float health) {
		this.health = health;
	}
	
	public float getHealth() {
		return health;
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
			BodyHelper.setPointing(getBody(), target.getBody().getWorldCenter(), delta, 10);
		}
		zSprite.setOriginCenter();
	}
	
	public void setMass(float mass) {
		massData.mass = mass;
	}

	@Override
	public MassData getMassData() {
		return massData;
	}

	@Override
	public void damage(float damage) {
		health -= damage;
		if (health <= 0) {
			g.removeEntity(this);
		}
	}

	@Override
	public Optional<Sprite> getSprite() {
		return Optional.of(zSprite);
	}
}
