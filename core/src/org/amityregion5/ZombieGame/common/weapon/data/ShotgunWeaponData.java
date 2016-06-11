package org.amityregion5.ZombieGame.common.weapon.data;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ShotgunWeaponData extends WeaponData {
	//The number of shots to shoot
	private int		shots;
	//The spread of the shots
	private double	spread;

	/**
	 * @return the shots
	 */
	public int getShots() {
		return shots;
	}

	/**
	 * @param shots
	 *            the shots to set
	 */
	public void setShots(int shots) {
		this.shots = shots;
	}

	/**
	 * @return the spread
	 */
	public double getSpread() {
		return spread;
	}

	/**
	 * @param spread
	 *            the spread to set
	 */
	public void setSpread(double spread) {
		this.spread = spread;
	}
	
	public static class Deserializor implements JsonDeserializer<ShotgunWeaponData> {
		@Override
		public ShotgunWeaponData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return deserialize(json, typeOfT, context, new ShotgunWeaponData());
		}
		public static ShotgunWeaponData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context, ShotgunWeaponData wd) throws JsonParseException {
			WeaponData.Deserializor.deserialize(json, typeOfT, context, wd);
			JsonObject o = json.getAsJsonObject();
			wd.shots = WeaponDataUtils.getClampedInt(o, "shots", 1, Integer.MAX_VALUE, 1);
			wd.spread = WeaponDataUtils.getClampedDouble(o, "spread", 0, Double.MAX_VALUE, 0);
			return wd;
		}
	}
}
