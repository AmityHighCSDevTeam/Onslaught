package io.github.AmityHighCSDevTeam.ZombieGame.common.weapon;

public interface IWeapon {
	public void setWeaponPrice(double price);
	public double getWeaponPrice();
	
	public void setAmmoPrice(double price);
	public double getAmmoPrice();
	
	public void setName(String name);
	public String getName();
	
	public void setDescription(String desc);
	public String getDescription();
	
	public void setBulletDamage(double damage);
	public double getBulletDamage();
	
	public void setBulletSpeed(double spd);
	public double getBulletSpeed();

	public String getAmmoString();
	
	//public boolean onUse(Point start, Point mousePos, World world); TODO: Create World Class
	
	public void purchaseAmmo();
	
	public void reload();
	
	public void tick(float delta);
}
