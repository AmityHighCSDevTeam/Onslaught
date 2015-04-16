/**
 * 
 */
package org.amityregion5.ZombieGame.common.entity;

import java.util.Optional;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.MathHelper;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
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
public class EntityLantern implements IEntity, Disposable {
	
	public static final Color LIGHT_COLOR = new Color(1, 1, 1, 0.6f);
	private Body body;
	private float friction;
	private Game g;
	private MassData massData;
	private Light light;
	private ConeLight mainLight;
	private ConeLight secondary;
	private RayHandler rayH;
	
	public EntityLantern(Game g, RayHandler rh) {
		this.g = g;
		rayH = rh;
		massData = new MassData();
	}

	@Override
	public void setShape(Shape e) {}

	@Override
	public Shape getShape() {	
		CircleShape shape = new CircleShape();
		shape.setRadius(0.05f);
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
		return 0;
	}

	@Override
	public void setSpeed(float f) {
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
		if (mainLight == null) {
			mainLight = new ConeLight(rayH, 200, LIGHT_COLOR, 15, getBody().getWorldCenter().x, getBody().getWorldCenter().y, 0, 100/2);
			secondary = new ConeLight(rayH, 200, LIGHT_COLOR, 15, getBody().getWorldCenter().x, getBody().getWorldCenter().y, 180, 260/2);
		}
		
		Optional<IEntity> closestEntity = g.getEntities().stream().filter((e) -> e != this)
				/*.filter((e) -> e.getBody().getWorldCenter().dst2(body.getWorldCenter()) < 20)*/.min((e1, e2) -> {
			return Math.round(e1.getBody().getWorldCenter().dst2(body.getWorldCenter()) - e2.getBody().getWorldCenter().dst2(body.getWorldCenter()));
		});
		
		if (closestEntity.isPresent()) {
			mainLight.setActive(true);
			secondary.setActive(true);
			light.setActive(false);
			
			mainLight.setDirection((float) Math.toDegrees(MathHelper.getDirBetweenPoints(body.getWorldCenter(), closestEntity.get().getBody().getWorldCenter())));
			mainLight.setPosition(getBody().getWorldCenter());
			secondary.setDirection((float) (180 + Math.toDegrees(MathHelper.getDirBetweenPoints(body.getWorldCenter(), closestEntity.get().getBody().getWorldCenter()))));
			secondary.setPosition(getBody().getWorldCenter());
		} else {
			mainLight.setActive(false);
			secondary.setActive(false);
			light.setActive(true);
			light.setPosition(getBody().getWorldCenter());
		}
	}
	
	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
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
		g.removeEntity(this);
		light.remove();
		mainLight.remove();
		secondary.remove();
	}

	@Override
	public Optional<Sprite> getSprite() {
		return Optional.ofNullable(null);
	}
}
