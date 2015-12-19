package org.amityregion5.ZombieGame.common.weapon.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
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
	public void onUse(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack, boolean isMouseJustDown) {}

	@Override
	public void purchaseAmmo(PlayerModel player, WeaponStack stack) {}

	@Override
	public void reload(WeaponStack stack, Game game, PlayerModel firing) {}

	@Override
	public void tick(float delta, WeaponStack stack) {}

	@Override
	public Map<String, String> getWeaponDataDescriptors(int level) {
		return new HashMap<String, String>();
	}

	@Override
	public String getID() {
		return "NullWeapon";
	}

	@Override
	public List<String> getTags() {
		return new ArrayList<String>();
	}

	@Override
	public String getStatus(WeaponStack stack) {
		return "NO WEAPON";
	}
}
