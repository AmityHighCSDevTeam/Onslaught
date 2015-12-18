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
		//Create array
		data = new Array<WeaponData>();

		//Loop through each object
		for (Object obj : arr) {
			JSONObject o = (JSONObject) obj;
			//Load the weapon data
			WeaponData d = new WeaponData(o);
			//Add it to the list
			data.add(d);
		}
		//Succeeded
		return true;
	}
}
