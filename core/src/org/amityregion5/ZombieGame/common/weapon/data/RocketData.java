package org.amityregion5.ZombieGame.common.weapon.data;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class RocketData extends GrenadeData {
	//The acceleration of the rocket
	private double acceleration;

	/**
	 * @return the acceleration
	 */
	public double getAcceleration() {
		return acceleration;
	}

	/**
	 * @param acceleration
	 *            the acceleration to set
	 */
	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}
	
	public static class Deserializor implements JsonDeserializer<RocketData> {
		@Override
		public RocketData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return deserialize(json, typeOfT, context, new RocketData());
		}
		public static RocketData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context, RocketData wd) throws JsonParseException {
			GrenadeData.Deserializor.deserialize(json, typeOfT, context, wd);
			JsonObject o = json.getAsJsonObject();
			wd.acceleration = WeaponDataUtils.getClampedDouble(o, "accel", 0, Double.MAX_VALUE, 0);
			return wd;
		}
	}
}
