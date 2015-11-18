package org.amityregion5.ZombieGame.common.weapon.types;

import java.util.List;
import java.util.Map;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.data.IWeaponDataBase;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;

/**
 * @author sergeys
 */
public interface IWeapon {

	public String getName();

	public String getDescription();

	public String getID();

	public Map<String, String> getWeaponDataDescriptors(int level);

	public List<String> getTags();

	public boolean loadWeapon(JSONObject json);

	public IWeaponDataBase getWeaponData(int level);

	public int getNumLevels();

	// Converts the gun's data into a string used for ammo display
	/**
	 * @return the string to display for the ammo
	 */
	public String getAmmoString(WeaponStack stack);

	public void onUse(Vector2 end, Game game, PlayerModel playerModel, double maxFireDegrees, WeaponStack stack,
			boolean isMouseJustDown);

	public void purchaseAmmo(PlayerModel playerModel, WeaponStack stack);

	public void reload(WeaponStack stack, Game game, PlayerModel firing);

	// Used by certain weapons to do stuff when not being fired
	/**
	 * @param delta
	 *            amount of time since last tick
	 */
	public void tick(float delta, WeaponStack stack);
}
