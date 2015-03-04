/**
 * 
 */
package org.amityregion5.ZombieGame.common.entity;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.BodyHelper;
import org.amityregion5.ZombieGame.common.weapon.IWeapon;
import org.amityregion5.ZombieGame.common.weapon.NullWeapon;

import box2dLight.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author savelyevse17
 *
 */
public class EntityPlayer implements IEntity, Disposable {

	private Body body;
	private float speed, friction;
	private MassData massData;
	private Light light;
	private IWeapon currentWeapon;
	private Vector2 mousePos;
	private Game g;

	public EntityPlayer(Game g) {
		massData = new MassData();
		this.g = g;
		if (ZombieGame.instance.weaponRegistry.getWeapons().size > 0) {
			currentWeapon = ZombieGame.instance.weaponRegistry.getWeapons().first();
		} else {
			currentWeapon = new NullWeapon();
		}
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
		mousePos = b.getPosition();
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
		if (Gdx.input.isKeyPressed(Keys.W)) {
			getBody().applyForceToCenter(new Vector2(0, getSpeed()), true);
		}	
		if (Gdx.input.isKeyPressed(Keys.S)) {
			getBody().applyForceToCenter(new Vector2(0, -getSpeed()), true);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			getBody().applyForceToCenter(new Vector2(getSpeed(),0), true);
		}	
		if (Gdx.input.isKeyPressed(Keys.A)) {
			getBody().applyForceToCenter(new Vector2(-getSpeed(), 0), true);
		}
		if (Gdx.input.isKeyJustPressed(Keys.F)) { 
			light.setActive(!light.isActive());
		}
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			currentWeapon.onUse(mousePos, g, this, 15);
		}
		if (Gdx.input.isKeyJustPressed(Keys.C)) {
			currentWeapon = ZombieGame.instance.weaponRegistry.getWeapons().get((ZombieGame.instance.weaponRegistry.getWeapons().indexOf(currentWeapon, true)+1 >= ZombieGame.instance.weaponRegistry.getWeapons().size ? 0 : ZombieGame.instance.weaponRegistry.getWeapons().indexOf(currentWeapon, true)+1));
		}
		currentWeapon.tick(delta);
		BodyHelper.setPointing(getBody(), mousePos, delta, 10);
		light.setDirection((float) Math.toDegrees(getBody().getAngle()));
		light.setPosition(getBody().getWorldCenter());
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
		// TODO Auto-generated method stub

	}
	
	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
	}
	
	public void setMousePos(Vector2 mousePos) {
		this.mousePos = mousePos;
	}
}
