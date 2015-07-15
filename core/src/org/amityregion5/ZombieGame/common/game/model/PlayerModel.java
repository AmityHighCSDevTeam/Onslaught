package org.amityregion5.ZombieGame.common.game.model;

import java.util.ArrayList;
import java.util.List;

import org.amityregion5.ZombieGame.client.game.HealthBarDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.client.game.TextureRegistry;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.BodyHelper;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

import box2dLight.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class PlayerModel implements IEntityModel<EntityPlayer> {
	private EntityPlayer 		entity;
	private double				money			= 1000;
	private Vector2				mousePos;
	private List<WeaponStack>	weapons;
	private int					currentWeapon	= 0;
	private InGameScreen 		screen;
	private Game				g;
	private Light				light, circleLight;
	private SpriteDrawingLayer	sprite;
	private float health, maxHealth, speed;

	public PlayerModel(EntityPlayer entity, Game g, InGameScreen screen, double startMoney) {
		this.entity = entity;
		weapons = new ArrayList<WeaponStack>();
		this.g = g;
		this.money = startMoney;
		this.screen = screen;
		health = 100;
		maxHealth = 100;

		sprite = new SpriteDrawingLayer(new Sprite(TextureRegistry.getTexturesFor("*/Players/**.png").get(0)));
	}
	
	public void tick(float delta) {
		if (screen.getCurrentWindow() == null) {
			if (Gdx.input.isKeyPressed(Keys.W)) {
				entity.getBody().applyForceToCenter(new Vector2(0, getSpeed()), true);
			}
			if (Gdx.input.isKeyPressed(Keys.S)) {
				entity.getBody().applyForceToCenter(new Vector2(0, -getSpeed()), true);
			}
			if (Gdx.input.isKeyPressed(Keys.D)) {
				entity.getBody().applyForceToCenter(new Vector2(getSpeed(), 0), true);
			}
			if (Gdx.input.isKeyPressed(Keys.A)) {
				entity.getBody().applyForceToCenter(new Vector2(-getSpeed(), 0), true);
			}
			if (Gdx.input.isKeyJustPressed(Keys.F)) {
				getLight().setActive(!getLight().isActive());
				getCircleLight().setActive(getLight().isActive());
			}
			if (Gdx.input.isKeyJustPressed(Keys.B)) {
				weapons.get(currentWeapon).purchaseAmmo(this);
			}
			if (Gdx.input.isKeyJustPressed(Keys.R)) {
				weapons.get(currentWeapon).reload();
			}
			if (Gdx.input.isButtonPressed(Buttons.LEFT) && weapons.size() > 0) {
				weapons.get(currentWeapon).onUse(mousePos, g, this, 15);
			}
			if (Gdx.input.isKeyJustPressed(Keys.C) && weapons.size() > 0) {
				currentWeapon++;
				currentWeapon %= weapons.size();
			}
			BodyHelper.setPointing(entity.getBody(), mousePos, delta, 10);
			getLight().setDirection((float) Math.toDegrees(entity.getBody().getAngle()));
			getLight().setPosition(entity.getBody().getWorldCenter());
			//entity.getCircleLight().setDirection((float) Math.toDegrees(entity.getBody().getAngle()));
			getCircleLight().setPosition(entity.getBody().getWorldCenter());
		}
		
		if (currentWeapon < weapons.size() && currentWeapon >= 0) {
			weapons.get(currentWeapon).tick(delta);
		}
		sprite.getSprite().setOriginCenter();
	}
	

	public void setMousePos(Vector2 mousePos) {
		this.mousePos = mousePos;
	}

	public WeaponStack getCurrentWeapon() {
		if (currentWeapon < weapons.size() && currentWeapon >= 0) {
			return weapons.get(currentWeapon);
		}
		return new WeaponStack(new NullWeapon());
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}
	
	
	public List<WeaponStack> getWeapons() {
		return weapons;
	}

	/**
	 * @return the entity
	 */
	@Override
	public EntityPlayer getEntity() {
		return entity;
	}

	@Override
	public void dispose() {
		light.remove();
		circleLight.remove();
		entity.dispose();
	}
	

	@Override
	public void damage(float damage, IEntityModel<?> source) {
		health -= damage;
	}

	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
	}
	
	public void setCircleLight(Light circleLight) {
		this.circleLight = circleLight;
	}
	
	public Light getCircleLight() {
		return circleLight;
	}

	@Override
	public IDrawingLayer[] getDrawingLayers() {
		return new IDrawingLayer[] {sprite, HealthBarDrawingLayer.instance};
	}

	@Override
	public float getHealth() {
		return health;
	}

	@Override
	public float getMaxHealth() {
		return maxHealth;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public boolean isHostile() {
		return false;
	}
}
