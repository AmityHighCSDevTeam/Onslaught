package org.amityregion5.ZombieGame.common.weapon.types;

import org.amityregion5.ZombieGame.common.weapon.data.WeaponData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.utils.Array;

/**
 * @author sergeys
 */
public class BasicGun extends AbstractWeapon<WeaponData> {

	@Override
	protected boolean loadWeaponData(JSONArray arr) {
		data = new Array<WeaponData>();

		for (Object obj : arr) {
			JSONObject o = (JSONObject) obj;
			WeaponData d = new WeaponData(o);
			data.add(d);
		}
		return true;
	}
}
