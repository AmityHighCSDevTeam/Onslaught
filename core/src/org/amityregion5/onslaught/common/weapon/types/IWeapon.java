package org.amityregion5.onslaught.common.weapon.types;

import java.util.List;
import java.util.Map;

import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;
import org.amityregion5.onslaught.common.weapon.WeaponStack;
import org.amityregion5.onslaught.common.weapon.data.IWeaponDataBase;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;

/**
 * @author sergeys
 */
public interface IWeapon {

	public String getName();

	public String getDescription();

	public String getID();

	public Map<String, String> getWeaponDataDescriptors(int level);

	public List<String> getTags();

	public boolean loadWeapon(JsonObject json, String pathName);
	
	public String getPathName();

	public IWeaponDataBase getWeaponData(int level);

	public int getNumLevels();

	// Converts the gun's data into a string used for ammo display
	/**
	 * @return the string to display for the ammo
	 */
	public String getAmmoString(WeaponStack stack);

	public void onUse(Vector2 end, Game game, PlayerModel playerModel, double maxFireDegrees, WeaponStack stack, boolean isMouseJustDown);

	public void purchaseAmmo(PlayerModel playerModel, WeaponStack stack);

	public void reload(WeaponStack stack, Game game, PlayerModel firing);

	// Used by certain weapons to do stuff when not being fired
	/**
	 * @param delta
	 *            amount of time since last tick
	 */
	public void tick(float delta, WeaponStack stack);
	
	public String getStatus(WeaponStack stack);
}
