package org.amityregion5.ZombieGame.common.weapon;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.PlayerModel;
import org.amityregion5.ZombieGame.common.weapon.data.IWeaponDataBase;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;

import com.badlogic.gdx.math.Vector2;

public class WeaponStack {
	private int	level, ammo, totalAmmo;
	private double	cooldown, warmup;
	private boolean	warmingUp;
	private IWeapon	weapon;
	
	//warmup stuffs
	private Vector2 warmupEnd;
	private Game warmupGame;
	private PlayerModel warmupFiring;
	private double warmupMaxFireDegrees;

	public WeaponStack(IWeapon weapon) {
		this.weapon = weapon;
	}

	/**
	 * @return the weapon
	 */
	public IWeapon getWeapon() {
		return weapon;
	}

	/**
	 * @param weapon
	 *            the weapon to set
	 */
	public void setWeapon(IWeapon weapon) {
		this.weapon = weapon;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the ammo
	 */
	public int getAmmo() {
		return ammo;
	}

	/**
	 * @param ammo
	 *            the ammo to set
	 */
	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}

	/**
	 * @return the totalAmmo
	 */
	public int getTotalAmmo() {
		return totalAmmo;
	}

	/**
	 * @param totalAmmo
	 *            the totalAmmo to set
	 */
	public void setTotalAmmo(int totalAmmo) {
		this.totalAmmo = totalAmmo;
	}

	/**
	 * @return the cooldown
	 */
	public double getCooldown() {
		return cooldown;
	}

	/**
	 * @param cooldown
	 *            the cooldown to set
	 */
	public void setCooldown(double cooldown) {
		this.cooldown = cooldown;
	}

	/**
	 * @return the warmup
	 */
	public double getWarmup() {
		return warmup;
	}

	/**
	 * @param warmup
	 *            the warmup to set
	 */
	public void setWarmup(double warmup) {
		this.warmup = warmup;
	}

	/**
	 * @return the warmingUp
	 */
	public boolean isWarmingUp() {
		return warmingUp;
	}

	/**
	 * @param warmingUp
	 *            the warmingUp to set
	 */
	public void setWarmingUp(boolean warmingUp) {
		this.warmingUp = warmingUp;
	}

	/**
	 * Tick this weapon
	 * 
	 * @param delta
	 *            time since last tick
	 */
	public void tick(float delta) {
		weapon.tick(delta, this);
	}

	public void purchaseAmmo(PlayerModel playerModel) {
		weapon.purchaseAmmo(playerModel, this);
	}

	public void reload(Game game, PlayerModel playerModel) {
		weapon.reload(this, game, playerModel);
	}

	public void onUse(Vector2 end, Game game, PlayerModel playerModel,
			int maxDegrees, boolean isMouseJustDown) {
		weapon.onUse(end, game, playerModel, maxDegrees, this, isMouseJustDown);
	}

	public String getAmmoString() {
		return weapon.getAmmoString(this);
	}
	
	public String getIconTextureName() {
		return weapon.getWeaponData(level).getIconTextureString();
	}
	
	public String getGameTextureName() {
		return weapon.getWeaponData(level).getGameTextureString();
	}
	
	@Override
	public String toString() {
		return "WeaponStack{Weapon:" + weapon.getName() + ",Level:" + level + "}";
	}

	/**
	 * @return the warmupEnd
	 */
	public Vector2 getWarmupEnd() {
		return warmupEnd;
	}

	/**
	 * @param warmupEnd the warmupEnd to set
	 */
	public void setWarmupEnd(Vector2 warmupEnd) {
		this.warmupEnd = warmupEnd;
	}

	/**
	 * @return the warmupGame
	 */
	public Game getWarmupGame() {
		return warmupGame;
	}

	/**
	 * @param warmupGame the warmupGame to set
	 */
	public void setWarmupGame(Game warmupGame) {
		this.warmupGame = warmupGame;
	}

	/**
	 * @return the warmupFiring
	 */
	public PlayerModel getWarmupFiring() {
		return warmupFiring;
	}

	/**
	 * @param warmupFiring the warmupFiring to set
	 */
	public void setWarmupFiring(PlayerModel warmupFiring) {
		this.warmupFiring = warmupFiring;
	}

	/**
	 * @return the warmupMaxFireDegrees
	 */
	public double getWarmupMaxFireDegrees() {
		return warmupMaxFireDegrees;
	}

	/**
	 * @param warmupMaxFireDegrees the warmupMaxFireDegrees to set
	 */
	public void setWarmupMaxFireDegrees(double warmupMaxFireDegrees) {
		this.warmupMaxFireDegrees = warmupMaxFireDegrees;
	}
	
	public IWeaponDataBase getWeaponDataBase() {
		return weapon.getWeaponData(level);
	}
}
