package org.amityregion5.ZombieGame.common.weapon;

import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.game.Game;

import com.badlogic.gdx.math.Vector2;

/**
 * 
 * @author sergeys
 *
 */
public interface IWeapon {
	//Used for weapon price in store
	/**
	 * 
	 * @param price the price to set for the weapon
	 */
	public void setWeaponPrice(double price);
	/**
	 * 
	 * @return the weapon's price
	 */
	public double getWeaponPrice();
	
	//Used for ammo price (Might change this to ask for an ammo type)
	/**
	 * 
	 * @param price the price of the ammo
	 */
	public void setAmmoPrice(double price);
	/**
	 * 
	 * @return the price of the ammo
	 */
	public double getAmmoPrice();
	
	//Used for anything that requires the name of the gun
	/**
	 * 
	 * @param name the name of the gun
	 */
	public void setName(String name);
	/**
	 * 
	 * @return the name of the gun
	 */
	public String getName();
	
	//A slightly detailed description
	/**
	 * 
	 * @param desc the description of the gun
	 */
	public void setDescription(String desc);
	/**
	 * 
	 * @return the description of the gun
	 */
	public String getDescription();
	
	//Might change this to ask for a bullet
	/**
	 * 
	 * @param damage the damage the bullet will do
	 */
	public void setBulletDamage(double damage);
	/**
	 * 
	 * @return the damage the bullet will do
	 */
	public double getBulletDamage();
	
	//Might change this to ask for a bullet
	/**
	 * 
	 * @param spd the speed the bullet will move at
	 */
	public void setBulletSpeed(double spd);
	/**
	 * 
	 * @return the speed the bullet will move at
	 */
	public double getBulletSpeed();
	
	//The delay between clicking the shoot button and it happenening
	/**
	 * 
	 * @param delay the delay between clicking shoot and shooting
	 */
	public void setPreFireDelay(double delay);
	/**
	 * 
	 * @return the delay between clicking shoot and shooting
	 */
	public double getPreFireDelay();
	
	//The delay between shooting and the next shot you can take
	/**
	 * 
	 * @param delay the delay between shooting and the next shot you can take
	 */
	public void setPostFireDelay(double delay);
	/**
	 * 
	 * @return the delay between shooting and the next shot you can take
	 */
	public double getPostFireDelay();

	//Converts the gun's data into a string used for ammo display
	/**
	 * 
	 * @return the string to display for the ammo
	 */
	public String getAmmoString();
	
	public void onUse(Vector2 end, Game game, EntityPlayer firing, double maxFireDegrees);
	
	public void purchaseAmmo();
	
	public void reload();
	
	//Used by certain weapons to do stuff when not being fired
	/**
	 * 
	 * @param delta amount of time since last tick
	 */
	public void tick(float delta);
	
	public double getAccuracyDeg();
	public void setAccuracyDeg(double deg);
}
