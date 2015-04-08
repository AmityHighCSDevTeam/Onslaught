package org.amityregion5.ZombieGame.common.weapon;

import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.game.Game;

import com.badlogic.gdx.math.Vector2;

public final class NullWeapon implements IWeapon {

	@Override
	public void setWeaponPrice(double price) {
	}

	@Override
	public double getWeaponPrice() {
		return 0;
	}

	@Override
	public void setAmmoPrice(double price) {
	}

	@Override
	public double getAmmoPrice() {
		return 0;
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setDescription(String desc) {
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void setBulletDamage(double damage) {
	}

	@Override
	public double getBulletDamage() {
		return 0;
	}

	@Override
	public void setBulletSpeed(double spd) {
	}

	@Override
	public double getBulletSpeed() {
		return 0;
	}

	@Override
	public void setPreFireDelay(double delay) {
	}

	@Override
	public double getPreFireDelay() {
		return 0;
	}

	@Override
	public void setPostFireDelay(double delay) {
	}

	@Override
	public double getPostFireDelay() {
		return 0;
	}

	@Override
	public String getAmmoString() {
		return null;
	}

	@Override
	public void onUse(Vector2 end, Game game, EntityPlayer firing,
			double maxFireDegrees) {
	}

	@Override
	public void reload() {
	}

	@Override
	public void tick(float delta) {
	}

	@Override
	public double getAccuracyDeg() {
		return 0;
	}

	@Override
	public void setAccuracyDeg(double deg) {
	}

	@Override
	public int getMaxAmmo() {
		return 0;
	}

	@Override
	public void setMaxAmmo(int ammo) {
	}

	@Override
	public void purchaseAmmo(EntityPlayer player) {
	}
}
