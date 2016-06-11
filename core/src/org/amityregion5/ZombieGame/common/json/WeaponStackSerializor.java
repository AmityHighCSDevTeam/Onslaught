package org.amityregion5.ZombieGame.common.json;

import java.lang.reflect.Type;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WeaponStackSerializor implements JsonSerializer<WeaponStack>, JsonDeserializer<WeaponStack> {

	@Override
	public JsonElement serialize(WeaponStack src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();

		obj.addProperty("id", src.getWeapon().getID());
		obj.addProperty("level", src.getLevel());
		obj.addProperty("ammo", src.getAmmo());
		obj.addProperty("totalAmmo", src.getTotalAmmo());
		obj.addProperty("cooldown", src.getPostFire());

		return obj;
	}


	@Override
	public WeaponStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		WeaponStack stack = new WeaponStack(ZombieGame.instance.weaponRegistry.getWeaponFromID(obj.get("id").getAsString()));
		
		stack.setLevel(obj.get("level").getAsInt());
		stack.setAmmo(obj.get("ammo").getAsInt());
		stack.setTotalAmmo(obj.get("totalAmmo").getAsInt());
		stack.setPostFire(obj.get("cooldown").getAsDouble());

		return stack;
	}
}
