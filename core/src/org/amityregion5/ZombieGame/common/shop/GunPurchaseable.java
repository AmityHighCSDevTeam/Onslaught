package org.amityregion5.ZombieGame.common.shop;

import java.util.Map;
import java.util.Optional;

import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.types.IWeapon;
import org.amityregion5.ZombieGame.common.weapon.types.NullWeapon;

public class GunPurchaseable implements IPurchaseable {

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
		int level = 0;

		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS)->wS.getWeapon()==gun).findAny();

		if (ws.isPresent()) {
			level = ws.get().getLevel();
		}

		Map<String, String> currLev = gun.getWeaponDataDescriptors(level);

		if (gun.getWeaponData(level).getBuff() != null) {
			for (String mBuff : gun.getWeaponData(level).getBuff().getMultiplicative().keySet()) {
				currLev.put(mBuff, gun.getWeaponData(level).getBuff().getMult(mBuff)*100 + "%");
			}

			for (String aBuff : gun.getWeaponData(level).getBuff().getAdditive().keySet()) {
				currLev.put(aBuff, (gun.getWeaponData(level).getBuff().getAdd(aBuff) > 0 ? "+" : "") + gun.getWeaponData(level).getBuff().getAdd(aBuff));
			}
		}

		return currLev;
	}

	@Override
	public Map<String, String> getNextDescriptors(PlayerModel player) {
		int level = 0;

		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS)->wS.getWeapon()==gun).findAny();

		if (ws.isPresent()) {
			level = ws.get().getLevel() + 1;
		}

		if (!hasNextLevel(player) || level == 0) {
			return null;
		}

		Map<String, String> nextLev = gun.getWeaponDataDescriptors(level);

		if (gun.getWeaponData(level).getBuff() != null) {
			for (String mBuff : gun.getWeaponData(level).getBuff().getMultiplicative().keySet()) {
				nextLev.put(mBuff, gun.getWeaponData(level).getBuff().getMult(mBuff)*100 + "%");
			}

			for (String aBuff : gun.getWeaponData(level).getBuff().getAdditive().keySet()) {
				nextLev.put(aBuff, (gun.getWeaponData(level).getBuff().getAdd(aBuff) > 0 ? "+" : "") + gun.getWeaponData(level).getBuff().getAdd(aBuff));
			}
		}

		return nextLev;
	}

	@Override
	public boolean hasNextLevel(PlayerModel player) {
		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS)->wS.getWeapon()==gun).findAny();

		if (ws.isPresent() && ws.get().getLevel() + 1 < ws.get().getWeapon().getNumLevels()) {
			return true;
		}
		return !ws.isPresent();
	}

	@Override
	public int getCurrentLevel(PlayerModel player) {
		int level = 0;

		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS)->wS.getWeapon()==gun).findAny();

		if (ws.isPresent()) {
			level = ws.get().getLevel();
		} else {
			return -1;
		}

		return level;
	}

	@Override
	public int getNumLevels() {
		return gun.getNumLevels();
	}

	@Override
	public double getPrice(PlayerModel player) {
		if (!hasNextLevel(player)) {
			return Double.POSITIVE_INFINITY;
		}
		return gun.getWeaponData(getCurrentLevel(player)+1).getPrice();
	}

	@Override
	public boolean canPurchase(PlayerModel player) {
		if (!hasNextLevel(player)) {
			return false;
		}
		return true;
	}

	@Override
	public void onPurchase(PlayerModel player) {
		Optional<WeaponStack> ws = player.getWeapons().parallelStream().filter((wS)->wS.getWeapon()==gun).findAny();

		if (ws.isPresent()) {
			player.removeTemporaryWeaponBuff();
			ws.get().setLevel(ws.get().getLevel()+1);
			player.addTemporaryWeaponBuff();
		} else {
			WeaponStack newWeap = new WeaponStack(gun);
			player.getWeapons().add(newWeap);
			if (player.getCurrentWeapon().getWeapon() instanceof NullWeapon) {
				player.removeTemporaryWeaponBuff();
				player.getHotbar()[player.getCurrWeapIndex()] = newWeap;
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
		return gun.getWeaponData(Math.min(getCurrentLevel(player)+1,getNumLevels()-1)).getIconTextureString();
	}

	@Override
	public int numContained(String[] sections, PlayerModel player) {
		int num = 0;

		for (String s : sections) {
			if (getName().toLowerCase().contains(s.toLowerCase())) num+=100;
			if (getDescription().toLowerCase().contains(s.toLowerCase())) num+=15;
			if (gun.getID().toLowerCase().contains(s.toLowerCase())) num+=50;
			num += gun.getTags().parallelStream().filter((k)->k.toLowerCase().contains(s.toLowerCase())).count();
			num += getCurrentDescriptors(player).keySet().parallelStream().filter((k)->k.toLowerCase().contains(s.toLowerCase())).count();
			num += getCurrentDescriptors(player).values().parallelStream().filter((k)->k.toLowerCase().contains(s.toLowerCase())).count();
			if (hasNextLevel(player) && getNextDescriptors(player) != null) {
				num += getNextDescriptors(player).keySet().parallelStream().filter((k)->k.toLowerCase().contains(s.toLowerCase())).count();
				num += getNextDescriptors(player).values().parallelStream().filter((k)->k.toLowerCase().contains(s.toLowerCase())).count();
			}
		}

		return num;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GunPurchaseable) {
			return gun == ((GunPurchaseable)obj).gun;
		}
		return false;
	}
}
