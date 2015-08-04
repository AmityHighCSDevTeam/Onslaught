package org.amityregion5.ZombieGame.common.weapon.types;

import java.util.HashMap;
import java.util.Map;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.PlayerModel;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.data.WeaponData;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;

public final class NullWeapon implements IWeapon {

	@Override
	public String getName() {
		return "No Weapon";
	}

	@Override
	public String getDescription() {
		return "This is not a weapon";
	}

	@Override
	public boolean loadWeapon(JSONObject json) {
		return false;
	}

	@Override
	public WeaponData getWeaponData(int level) {
		return null;
	}

	@Override
	public int getNumLevels() {
		return 0;
	}

	@Override
	public String getAmmoString(WeaponStack stack) {
		return "";
	}

	@Override
	public void onUse(Vector2 end, Game game, PlayerModel firing,
			double maxFireDegrees, WeaponStack stack) {
	}

	@Override
	public void purchaseAmmo(PlayerModel player, WeaponStack stack) {
	}

	@Override
	public void reload(WeaponStack stack) {
	}

	@Override
	public void tick(float delta, WeaponStack stack) {
	}

	@Override
	public Map<String, String> getWeaponDataDescriptors(int level) {
		return new HashMap<String, String>();
	}
}
