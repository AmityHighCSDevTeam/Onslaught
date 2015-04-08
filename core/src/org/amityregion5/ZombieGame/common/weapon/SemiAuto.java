package org.amityregion5.ZombieGame.common.weapon;

import org.amityregion5.ZombieGame.common.bullet.BasicBullet;
import org.amityregion5.ZombieGame.common.entity.EntityPlayer;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;

import com.badlogic.gdx.math.Vector2;

/**
 * 
 * @author sergeys
 *
 */
public class SemiAuto implements IWeapon {
	
	//All the variables!
	private double weaponPrice, ammoPrice, bulletDamage, bulletSpeed, preFireDelay, postFireDelay, coolDown, warmUp, accuracyDeg, reloadTime;
	private String name, description;
	private boolean warmingUp;
	private int ammo, maxAmmo, totalAmmo;
	
	
	
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
		return getAmmo() + "/" + getMaxAmmo() + "/" + getTotalAmmo();
	}
	@Override
	public void purchaseAmmo(EntityPlayer player) {
		double ammoMoney = maxAmmo * ammoPrice;
		int amtToBuy = maxAmmo;
		if (ammoMoney > player.getMoney()) {
			amtToBuy = (int) (player.getMoney()/ammoMoney);
		}
		player.setMoney(player.getMoney() - amtToBuy * ammoPrice);
		totalAmmo+=amtToBuy;
	}	
	@Override
	public void reload() {
		if (ammo < maxAmmo) {
			int ammoNeeded = maxAmmo - ammo;
			if (ammoNeeded > totalAmmo) {
				ammoNeeded = totalAmmo;
			}
			if (ammoNeeded > 0) {
				coolDown+=reloadTime;
				ammo+=ammoNeeded;
				totalAmmo-=ammoNeeded;
			}
		}
	}	
	@Override
	public void tick(float delta) {
		if (coolDown > 0) {
			coolDown -= delta;
		}
		if (warmUp > 0) {
			warmUp -= delta;
		}
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
	@Override
	public void onUse(Vector2 end, Game game, EntityPlayer firing, double maxFireDegrees){
		if (coolDown <= 0) {
			if (warmUp <= 0) {
				warmingUp = false;
				if (ammo > 0) {
					ammo--;
					double dir = MathHelper.clampAngleAroundCenter(firing.getBody().getAngle(), 
							MathHelper.getDirBetweenPoints(firing.getBody().getPosition(), end), 
							Math.toRadians(maxFireDegrees));
					
					dir -= Math.toRadians(getAccuracyDeg()/2);
					
					dir += Math.toRadians(game.getRandom().nextDouble()*getAccuracyDeg());
					
					dir = MathHelper.fixAngle(dir);
					
					
					Vector2 v = MathHelper.getEndOfLine(firing.getBody().getPosition(), firing.getShape().getRadius() - 0.01, dir);
					
					Vector2 bullVector = VectorFactory.createVector(200f, (float) dir);
					
					BasicBullet bull = new BasicBullet(game, v, (float)getBulletSpeed(), 18/1000f, (float)getBulletDamage(), bullVector);
					bull.setDir((float) dir);

					game.getActiveBullets().add(bull);
					game.getWorld().rayCast(bull, v, bullVector);
					bull.finishRaycast();
					
					coolDown += getPostFireDelay();
				} else {
					reload();
				}
			} else if (!warmingUp) {
				warmUp = getPreFireDelay();
				warmingUp = true;
			}
		}
	}
	@Override
	public double getAccuracyDeg() {
		return accuracyDeg;
	}
	@Override
	public void setAccuracyDeg(double deg) {
		accuracyDeg = deg;
	}
	@Override
	public int getMaxAmmo() {
		return maxAmmo;
	}
	public int getAmmo() {
		return ammo;
	}
	
	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}
	
	@Override
	public void setMaxAmmo(int maxAmmo) {
		this.maxAmmo = maxAmmo;
	}
	
	public void setTotalAmmo(int totalAmmo) {
		this.totalAmmo = totalAmmo;
	}
	
	public int getTotalAmmo() {
		return totalAmmo;
	}
}
