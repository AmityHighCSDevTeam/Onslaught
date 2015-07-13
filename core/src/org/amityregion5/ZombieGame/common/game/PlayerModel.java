package org.amityregion5.ZombieGame.common.game;

import java.util.ArrayList;
import java.util.List;

import org.amityregion5.ZombieGame.client.screen.InGameScreen;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.helper.BodyHelper;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class PlayerModel {
	private EntityPlayer 		entity;
	private double				money			= 10000;
	private Vector2				mousePos;
	private List<WeaponStack>	weapons;
	private int					currentWeapon	= 0;
	private InGameScreen 		screen;
	private Game				g;

	public PlayerModel(EntityPlayer entity, Game g, InGameScreen screen) {
		this.entity = entity;
		weapons = new ArrayList<WeaponStack>();
		this.g = g;
		this.screen = screen;
	}
	
	public void tick(float delta) {
		if (screen.getCurrentWindow() == null) {
			if (Gdx.input.isKeyPressed(Keys.W)) {
				entity.getBody().applyForceToCenter(new Vector2(0, entity.getSpeed()), true);
			}
			if (Gdx.input.isKeyPressed(Keys.S)) {
				entity.getBody().applyForceToCenter(new Vector2(0, -entity.getSpeed()), true);
			}
			if (Gdx.input.isKeyPressed(Keys.D)) {
				entity.getBody().applyForceToCenter(new Vector2(entity.getSpeed(), 0), true);
			}
			if (Gdx.input.isKeyPressed(Keys.A)) {
				entity.getBody().applyForceToCenter(new Vector2(-entity.getSpeed(), 0), true);
			}
			if (Gdx.input.isKeyJustPressed(Keys.F)) {
				entity.getLight().setActive(!entity.getLight().isActive());
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
			entity.getLight().setDirection((float) Math.toDegrees(entity.getBody().getAngle()));
			entity.getLight().setPosition(entity.getBody().getWorldCenter());
		}
		
		if (currentWeapon < weapons.size() && currentWeapon >= 0) {
			weapons.get(currentWeapon).tick(delta);
		}
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
	public EntityPlayer getEntity() {
		return entity;
	}
}
