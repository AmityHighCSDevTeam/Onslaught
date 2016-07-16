package org.amityregion5.onslaught.common.weapon.data;

import org.amityregion5.onslaught.Onslaught;

import com.google.gson.JsonObject;

public class WeaponDataUtils {
	public static double getClampedDouble(JsonObject o, String type, double min, double max, double def) {
		if (!o.has(type)) return def;
		double d = o.get(type).getAsDouble();
		if (d < min) {
			d = min;
			Onslaught.error("Weapon data error: " + type + " < " + min + "; set to " + min);
		} else if (d > max) {
			d = max;
			Onslaught.error("Weapon data error: " + type + " > " + max + "; set to " + max);
		}
		return d;
	}
	public static float getClampedFloat(JsonObject o, String type, float min, float max, float def) {
		if (!o.has(type)) return def;
		float d = o.get(type).getAsFloat();
		if (d < min) {
			d = min;
			Onslaught.error("Weapon data error: " + type + " < " + min + "; set to " + min);
		} else if (d > max) {
			d = max;
			Onslaught.error("Weapon data error: " + type + " > " + max + "; set to " + max);
		}
		return d;
	}
	public static int getClampedInt(JsonObject o, String type, int min, int max, int def) {
		if (!o.has(type)) return def;
		int i = o.get(type).getAsInt();
		if (i < min) {
			i = min;
			Onslaught.error("Weapon data error: " + type + " < " + min + "; set to " + min);
		} else if (i > max) {
			i = max;
			Onslaught.error("Weapon data error: " + type + " > " + max + "; set to " + max);
		}
		return i;
	}
}
