package org.amityregion5.ZombieGame.common.shop;

import java.util.Map;
import java.util.Optional;

import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

public class GunPurchaseable implements IPurchaseable {

	//The gun to buy
	private IWeapon gun;

	public GunPurchaseable(IWeapon gun) {
		this.gun = gun;
	}

	@Override
	public String getName() {
		return gun.getName();
	}

	@Override
	public String getDescription() {
		return gun.getDescription();
	}

	@Override
	public Map<String, String> getCurrentDescriptors(PlayerModel player) {
		//Get the current level
		int level = 0;

		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS) -> wS.getWeapon() == gun).findAny();

		if (ws.isPresent()) {
			level = ws.get().getLevel();
		}

		//Get base descriptors
		Map<String, String> currLev = gun.getWeaponDataDescriptors(level);

		//Apply buff descriptors
		if (gun.getWeaponData(level).getBuff() != null) {
			for (String mBuff : gun.getWeaponData(level).getBuff().getMultiplicative().keySet()) {
				currLev.put(mBuff, gun.getWeaponData(level).getBuff().getMult(mBuff) * 100 + "%");
			}

			for (String aBuff : gun.getWeaponData(level).getBuff().getAdditive().keySet()) {
				currLev.put(aBuff, (gun.getWeaponData(level).getBuff().getAdd(aBuff) > 0 ? "+" : "") + gun.getWeaponData(level).getBuff().getAdd(aBuff));
			}
		}

		return currLev;
	}

	@Override
	public Map<String, String> getNextDescriptors(PlayerModel player) {
		//Get level
		int level = 0;

		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS) -> wS.getWeapon() == gun).findAny();

		if (ws.isPresent()) {
			level = ws.get().getLevel() + 1;
		}

		if (!hasNextLevel(player) || level == 0) { return null; }

		//Get base descriptors
		Map<String, String> nextLev = gun.getWeaponDataDescriptors(level);

		//Apply buff desciptors
		if (gun.getWeaponData(level).getBuff() != null) {
			for (String mBuff : gun.getWeaponData(level).getBuff().getMultiplicative().keySet()) {
				nextLev.put(mBuff, gun.getWeaponData(level).getBuff().getMult(mBuff) * 100 + "%");
			}

			for (String aBuff : gun.getWeaponData(level).getBuff().getAdditive().keySet()) {
				nextLev.put(aBuff, (gun.getWeaponData(level).getBuff().getAdd(aBuff) > 0 ? "+" : "") + gun.getWeaponData(level).getBuff().getAdd(aBuff));
			}
		}

		return nextLev;
	}

	@Override
	public boolean hasNextLevel(PlayerModel player) {
		//Get the player's weapon stack of this level
		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS) -> wS.getWeapon() == gun).findAny();

		//If it exists and has a level to progress to return true
		if (ws.isPresent() && ws.get().getLevel() + 1 < ws.get().getWeapon().getNumLevels()) { return true; }
		//If it isnt present return false
		return !ws.isPresent();
	}

	@Override
	public int getCurrentLevel(PlayerModel player) {
		//Get weapon stack
		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS) -> wS.getWeapon() == gun).findAny();

		//If it exists
		if (ws.isPresent()) {
			//Return weapon stack's level
			return ws.get().getLevel();
		} else {
			//If it doesn't return -1;
			return -1;
		}
	}

	@Override
	public int getNumLevels() {
		return gun.getNumLevels();
	}

	@Override
	public double getPrice(PlayerModel player) {
		//If there are no levels make the price infinity
		if (!hasNextLevel(player)) { return Double.POSITIVE_INFINITY; }
		//Else return the price for the next level
		return gun.getWeaponData(getCurrentLevel(player) + 1).getPrice();
	}

	@Override
	public boolean canPurchase(PlayerModel player) {
		//Check if the player has any further levels to progress to
		if (!hasNextLevel(player)) { return false; }
		return true;
	}

	@Override
	public void onPurchase(PlayerModel player) {
		//Get the weapon stack
		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS) -> wS.getWeapon() == gun).findAny();

		//If it exists
		if (ws.isPresent()) {
			//Remove player's current weapon buffs (in case player holding the weapon)
			player.removeTemporaryWeaponBuff();
			//Increment the level
			ws.get().setLevel(ws.get().getLevel() + 1);
			//Add the weapon buffs
			player.addTemporaryWeaponBuff();
		} else {
			//Create a new weapon stack
			WeaponStack newWeap = new WeaponStack(gun);
			//Add it to the player's list of weapons
			player.getWeapons().add(newWeap);
			//If the player isn't holding a weapon
			if (player.getCurrentWeapon().getWeapon() instanceof NullWeapon) {
				//Remove any current buffs (should be none)
				player.removeTemporaryWeaponBuff();
				//Set the weapon in the hotbar slot
				player.getHotbar()[player.getCurrWeapIndex()] = newWeap;
				//Add buffs
				player.addTemporaryWeaponBuff();
			}
		}
	}

	@Override
	public boolean hasIcon() {
		return true;
	}

	@Override
	public String getIconName(PlayerModel player) {
		//Return the icons for the next level if it exists; else return for current level
		return gun.getWeaponData(Math.min(getCurrentLevel(player) + 1, getNumLevels() - 1)).getIconTextureString();
	}

	@Override
	public int numContained(String[] sections, PlayerModel player) {
		//Running count
		int num = 0;

		//For each section
		for (String s : sections) {
			//If it is in the name
			if (getName().toLowerCase().contains(s.toLowerCase())) {
				//increment count by 100
				num += 100;
			}
			//If in description
			if (getDescription().toLowerCase().contains(s.toLowerCase())) {
				//increment count by 15
				num += 15;
			}
			//If it is in the ID
			if (gun.getID().toLowerCase().contains(s.toLowerCase())) {
				//increment count by 50
				num += 50;
			}
			//For each time it appears in tags or descriptors; increment count by 1
			num += gun.getTags().parallelStream().filter((k) -> k.toLowerCase().contains(s.toLowerCase())).count();
			num += getCurrentDescriptors(player).keySet().parallelStream().filter((k) -> k.toLowerCase().contains(s.toLowerCase())).count();
			num += getCurrentDescriptors(player).values().parallelStream().filter((k) -> k.toLowerCase().contains(s.toLowerCase())).count();
			if (hasNextLevel(player) && getNextDescriptors(player) != null) {
				num += getNextDescriptors(player).keySet().parallelStream().filter((k) -> k.toLowerCase().contains(s.toLowerCase())).count();
				num += getNextDescriptors(player).values().parallelStream().filter((k) -> k.toLowerCase().contains(s.toLowerCase())).count();
			}
		}

		//Return total
		return num;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GunPurchaseable) { return gun == ((GunPurchaseable) obj).gun; }
		return false;
	}
	
	public IWeapon getGun() {
		return gun;
	}
}
