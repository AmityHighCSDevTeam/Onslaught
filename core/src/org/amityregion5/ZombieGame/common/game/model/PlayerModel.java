package org.amityregion5.ZombieGame.common.game.model;

import java.util.ArrayList;
import java.util.List;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.client.asset.SoundPlayingData;
import org.amityregion5.ZombieGame.client.asset.TextureRegistry;
import org.amityregion5.ZombieGame.client.game.HealthBarDrawingLayer;
import org.amityregion5.ZombieGame.client.game.IDrawingLayer;
import org.amityregion5.ZombieGame.client.game.PlayerExtrasDrawingLayer;
import org.amityregion5.ZombieGame.client.game.SpriteDrawingLayer;
import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.BodyHelper;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import box2dLight.Light;

public class PlayerModel implements IEntityModel<EntityPlayer> {
	private EntityPlayer 		entity;
	private double				money			= 1000;
	private double				screenJitter 	= 0;
	private Vector2				mousePos;
	private List<WeaponStack>	weapons;
	private WeaponStack[] 		hotbar;
	private int					currentWeapon	= 0;
	private InGameScreen 		screen;
	private Game				g;
	private Light				light, circleLight;
	private SpriteDrawingLayer	sprite;
	private PlayerExtrasDrawingLayer extras;
	private float health, maxHealth, speed;
	private boolean shootJustPressed = false;
	private List<SoundPlayingData> 	soundsToPlay;

	public PlayerModel(EntityPlayer entity, Game g, InGameScreen screen, double startMoney) {
		this.entity = entity;
		weapons = new ArrayList<WeaponStack>();
		this.g = g;
		this.money = startMoney;
		this.screen = screen;
		health = 100;
		maxHealth = 100;
		hotbar = new WeaponStack[3];

		sprite = new SpriteDrawingLayer(new Sprite(TextureRegistry.getTexturesFor("*/Players/**.png").get(0)));
		extras = new PlayerExtrasDrawingLayer(this);

		soundsToPlay = new ArrayList<SoundPlayingData>();
	}

	public void tick(float delta) {
		if (ZombieGame.instance.settings.getInput("Shoot").isDown() && screen.getCurrentWindow() == null) {
			if (weapons.size() > 0) {
				hotbar[currentWeapon].onUse(mousePos, g, this, 15, shootJustPressed);					
			}
			shootJustPressed = false;
		} else {
			shootJustPressed = true;
		}

		if (ZombieGame.instance.settings.getInput("Move_Up").isDown()) {
			entity.getBody().applyForceToCenter(new Vector2(0, getSpeed()), true);
		}
		if (ZombieGame.instance.settings.getInput("Move_Down").isDown()) {
			entity.getBody().applyForceToCenter(new Vector2(0, -getSpeed()), true);
		}
		if (ZombieGame.instance.settings.getInput("Move_Right").isDown()) {
			entity.getBody().applyForceToCenter(new Vector2(getSpeed(), 0), true);
		}
		if (ZombieGame.instance.settings.getInput("Move_Left").isDown()) {
			entity.getBody().applyForceToCenter(new Vector2(-getSpeed(), 0), true);
		}
		if (ZombieGame.instance.settings.getInput("Toggle_Flashlight").isJustDown()) {
			getLight().setActive(!getLight().isActive());
			//getCircleLight().setActive(getLight().isActive());
		}
		if (ZombieGame.instance.settings.getInput("Buy_Ammo").isJustDown()) {
			hotbar[currentWeapon].purchaseAmmo(this);
		}
		if (ZombieGame.instance.settings.getInput("Reload").isJustDown()) {
			hotbar[currentWeapon].reload(g, this);
		}

		BodyHelper.setPointing(entity.getBody(), mousePos, delta, 10);
		getLight().setDirection((float) Math.toDegrees(entity.getBody().getAngle()));

		if (ZombieGame.instance.settings.getInput("Hotbar_1").isJustDown()) {
			currentWeapon = 0;
		}
		if (ZombieGame.instance.settings.getInput("Hotbar_2").isJustDown()) {
			currentWeapon = 1;
		}
		if (ZombieGame.instance.settings.getInput("Hotbar_3").isJustDown()) {
			currentWeapon = 2;
		}

		getLight().attachToBody(entity.getBody());
		getCircleLight().attachToBody(entity.getBody());
		//getLight().setPosition(entity.getBody().getWorldCenter());
		//getCircleLight().setPosition(entity.getBody().getWorldCenter());

		if (currentWeapon < getHotbar().length && currentWeapon >= 0) {
			getHotbar()[currentWeapon].tick(delta);
		}
		sprite.getSprite().setOriginCenter();
		screenJitter *= 0.9;
	}


	public void setMousePos(Vector2 mousePos) {
		this.mousePos = mousePos;
	}

	public WeaponStack getCurrentWeapon() {
		return hotbar[currentWeapon];
		/*
		if (currentWeapon < weapons.size() && currentWeapon >= 0) {
			return weapons.get(currentWeapon);
		}
		return new WeaponStack(new NullWeapon());*/
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
	public float damage(float damage, IEntityModel<?> source) {
		float damageTaken = Math.min(damage, health);
		health -= damageTaken;
		return damageTaken; 
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
		return new IDrawingLayer[] {sprite, extras, HealthBarDrawingLayer.instance};
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

	public WeaponStack[] getHotbar() {
		for (int i = 0; i<hotbar.length; i++) {
			if (hotbar[i] == null) {
				hotbar[i] = new WeaponStack(new NullWeapon());
			}
		}
		return hotbar;
	}

	public int getCurrWeapIndex() {
		return currentWeapon;
	}

	public double getScreenVibrate() {
		return screenJitter;
	}

	public void setScreenVibrate(double screenJitter) {
		this.screenJitter = screenJitter;
	}

	public void playSound(SoundPlayingData sound) {
		soundsToPlay.add(sound);
	}

	public List<SoundPlayingData> getSoundsToPlay() {
		return soundsToPlay;
	}
}
