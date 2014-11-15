package org.amityregion5.ZombieGame.common.weapon;

/**
 * 
 * @author sergeys
 *
 */
public class SemiAuto implements IWeapon {
	
	//All the variables!
	private double weaponPrice, ammoPrice, bulletDamage, bulletSpeed, preFireDelay, postFireDelay;
	private String name, description;
	
	
	
	/**
	 * @return the preFireDelay
	 */
	@Override
	public double getPreFireDelay() {
		return preFireDelay;
	}
	/**
	 * @param preFireDelay the preFireDelay to set
	 */
	@Override
	public void setPreFireDelay(double preFireDelay) {
		this.preFireDelay = preFireDelay;
	}
	/**
	 * @return the postFireDelay
	 */
	@Override
	public double getPostFireDelay() {
		return postFireDelay;
	}
	/**
	 * @param postFireDelay the postFireDelay to set
	 */
	@Override
	public void setPostFireDelay(double postFireDelay) {
		this.postFireDelay = postFireDelay;
	}
	
	@Override
	public void setWeaponPrice(double price) {
		weaponPrice = price;
	}	
	@Override
	public double getWeaponPrice() {
		return weaponPrice;
	}	
	@Override
	public void setAmmoPrice(double ammo) {
		this.ammoPrice = ammo;
	}	
	@Override
	public double getAmmoPrice() {
		return ammoPrice;
	}	
	@Override
	public void setName(String name) {
		this.name = name;
	}	
	@Override
	public String getName() {
		return name;
	}	
	@Override
	public String getAmmoString() {
		return null;
	}
	@Override
	public void purchaseAmmo() {
	}	
	@Override
	public void reload() {
	}	
	@Override
	public void tick(float delta) {
	}
	@Override
	public void setDescription(String desc) {
		description = desc;
	}
	@Override
	public String getDescription() {
		return description;
	}
	@Override
	public void setBulletDamage(double damage) {
		bulletDamage = damage;
	}
	@Override
	public double getBulletDamage() {
		return bulletDamage;
	}
	@Override
	public void setBulletSpeed(double spd) {
		bulletSpeed = spd;
	}
	@Override
	public double getBulletSpeed() {
		return bulletSpeed;
	}	
}
