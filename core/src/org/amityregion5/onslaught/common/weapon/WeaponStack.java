package org.amityregion5.onslaught.common.weapon;

import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;
import org.amityregion5.onslaught.common.weapon.data.IWeaponDataBase;
import org.amityregion5.onslaught.common.weapon.types.IWeapon;

import com.badlogic.gdx.math.Vector2;

/**
 * A container for the weapon that a player has
 * @author sergeys
 *
 */
public class WeaponStack {
	
	private int		level; //The gun's level
	private int ammo; //The gun's ammo
	private int totalAmmo; //The gun's total ammo
	private double	postFire; //The cooldown value
	private double	preFire; //The warmup value
	private boolean	prefiring; //Is the gun currently warming up
	private IWeapon	weapon; //The gun

	//Warmup Information
	private Vector2		preFireEnd; // Target for the warmup
	private Game		preFireGame; //Game for the warmup
	private PlayerModel	preFireFiring; //The player for the warmup
	private double		preFireMaxFireDegrees; //The fire degrees for the warmup
	private int weaponTime; //A storage for the place in time that this weapon is in.

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
	 * @return the post fire cooldown
	 */
	public double getPostFire() {
		return postFire;
	}

	/**
	 * @param postFire
	 *            the post fire cooldown to set
	 */
	public void setPostFire(double postFire) {
		this.postFire = postFire;
	}

	/**
	 * @return the pre fire delay
	 */
	public double getPreFire() {
		return preFire;
	}

	/**
	 * @param preFire
	 *            the pre fire delay to set
	 */
	public void setPreFire(double preFire) {
		this.preFire = preFire;
	}

	/**
	 * @return the is it prefiring
	 */
	public boolean isPreFiring() {
		return prefiring;
	}

	/**
	 * @param prefiring
	 *            the prefiring to set
	 */
	public void setPreFiring(boolean prefiring) {
		this.prefiring = prefiring;
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

	public void onUse(Vector2 end, Game game, PlayerModel playerModel, int maxDegrees, boolean isMouseJustDown) {
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
		return preFireEnd;
	}

	/**
	 * @param warmupEnd
	 *            the warmupEnd to set
	 */
	public void setPreFireEnd(Vector2 warmupEnd) {
		this.preFireEnd = warmupEnd;
	}

	/**
	 * @return the warmupGame
	 */
	public Game getWarmupGame() {
		return preFireGame;
	}

	/**
	 * @param warmupGame
	 *            the warmupGame to set
	 */
	public void setPreFireGame(Game warmupGame) {
		this.preFireGame = warmupGame;
	}

	/**
	 * @return the warmupFiring
	 */
	public PlayerModel getWarmupFiring() {
		return preFireFiring;
	}

	/**
	 * @param warmupFiring
	 *            the warmupFiring to set
	 */
	public void setPreFireFiring(PlayerModel warmupFiring) {
		this.preFireFiring = warmupFiring;
	}

	/**
	 * @return the warmupMaxFireDegrees
	 */
	public double getWarmupMaxFireDegrees() {
		return preFireMaxFireDegrees;
	}

	/**
	 * @param warmupMaxFireDegrees
	 *            the warmupMaxFireDegrees to set
	 */
	public void setPreFireMaxFireDegrees(double warmupMaxFireDegrees) {
		this.preFireMaxFireDegrees = warmupMaxFireDegrees;
	}

	public IWeaponDataBase getWeaponDataBase() {
		return weapon.getWeaponData(level);
	}

	public String getID() {
		return weapon.getID();
	}

	public WeaponStatus getStatus() {
		return weapon.getStatus(this);
	}
	
	public void setWeaponTime(int weaponTime) {
		this.weaponTime = weaponTime;
	}
	
	public int getWeaponTime() {
		return weaponTime;
	}
}
