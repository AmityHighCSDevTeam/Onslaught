package org.amityregion5.ZombieGame.common.weapon.data;

import org.amityregion5.ZombieGame.ZombieGame;
import org.json.simple.JSONObject;

public class WeaponDataUtils {
	public static double getClampedDouble(JSONObject o, String type, double min, double max, double def) {
		if (!o.containsKey(type)) return def;
		double d = ((Number) o.get(type)).doubleValue();
		if (d < min) {
			d = min;
			ZombieGame.error("Weapon data error: " + type + " < " + min + "; set to " + min);
		} else if (d > max) {
			d = max;
			ZombieGame.error("Weapon data error: " + type + " > " + max + "; set to " + max);
		}
		return d;
	}
	public static float getClampedFloat(JSONObject o, String type, float min, float max, float def) {
		if (!o.containsKey(type)) return def;
		float d = ((Number) o.get(type)).floatValue();
		if (d < min) {
			d = min;
			ZombieGame.error("Weapon data error: " + type + " < " + min + "; set to " + min);
		} else if (d > max) {
			d = max;
			ZombieGame.error("Weapon data error: " + type + " > " + max + "; set to " + max);
		}
		return d;
	}
	public static int getClampedInt(JSONObject o, String type, int min, int max, int def) {
		if (!o.containsKey(type)) return def;
		int i = ((Number) o.get(type)).intValue();
		if (i < min) {
			i = min;
			ZombieGame.error("Weapon data error: " + type + " < " + min + "; set to " + min);
		} else if (i > max) {
			i = max;
			ZombieGame.error("Weapon data error: " + type + " > " + max + "; set to " + max);
		}
		return i;
	}
}
