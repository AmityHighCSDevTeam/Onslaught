package org.amityregion5.ZombieGame.common.weapon.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.IEntityModel;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.WeaponUtils;
import org.amityregion5.ZombieGame.common.weapon.data.IWeaponDataBase;
import org.amityregion5.ZombieGame.common.weapon.data.PlaceableWeaponData;
import org.amityregion5.ZombieGame.common.weapon.data.SoundData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Placeable implements IWeapon {

	//Registered placeable objects
	public static HashMap<String, BiFunction<Game, Vector2, IEntityModel<?>>> registeredObjects = new HashMap<String, BiFunction<Game, Vector2, IEntityModel<?>>>();

	// All the variables!
	protected String name; //The name of the rocket
	protected String description; //The description
	protected String id; //The unique ID
	protected List<String>					tags; //The tags it has
	protected Array<PlaceableWeaponData>	data; //The placeable data

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getAmmoString(WeaponStack stack) {
		return stack.getAmmo() + "/" + data.get(stack.getLevel()).getMaxAmmo() + "/" + stack.getTotalAmmo();
	}

	@Override
	public void purchaseAmmo(PlayerModel player, WeaponStack stack) {
		//Call utility function
		WeaponUtils.purchaseAmmo(player, stack, data.get(stack.getLevel()).getMaxAmmo(),
				data.get(stack.getLevel()).getAmmoPrice());
	}

	@Override
	public void reload(WeaponStack stack, Game game, PlayerModel firing) {
		//Call utility function
		WeaponUtils.reload(stack, game, firing, data.get(stack.getLevel()).getMaxAmmo(),
				data.get(stack.getLevel()).getReloadTime(), data.get(stack.getLevel()).getSounds());
	}

	@Override
	public void tick(float delta, WeaponStack stack) {
		//Call utility function
		WeaponUtils.tick(delta, stack, this::doPlace);
	}

	@Override
	public void onUse(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack, boolean isMouseJustDown) {
		//Call utility function
		WeaponUtils.onUse(end, game, firing, maxFireDegrees, stack, isMouseJustDown, data.get(stack.getLevel()).getWarmup(),
				false, data.get(stack.getLevel()).getPreFireDelay(),
				this::reload, this::doPlace);
	}

	protected void doPlace(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack) {
		//If the object is registered and within distance
		if (registeredObjects.containsKey(data.get(stack.getLevel()).getPlacingObject()) && 
				end.dst2(firing.getEntity().getBody().getWorldCenter()) <= data.get(stack.getLevel()).getMaxRange()*data.get(stack.getLevel()).getMaxRange()) {
			//Subtract ammo
			stack.setAmmo(stack.getAmmo() - 1);

			//Place the object
			game.addEntityToWorld(registeredObjects.get(data.get(stack.getLevel()).getPlacingObject()).apply(game, end), end.x, end.y);

			//Play sounds
			for (SoundData sound : data.get(stack.getLevel()).getSounds()) {
				if (sound.getTrigger().equals("fire")) {
					game.playSound(sound, firing.getEntity().getBody().getWorldCenter());
				}
			}
		}
	}

	@Override
	public IWeaponDataBase getWeaponData(int level) {
		return data.get(level);
	}

	@Override
	public int getNumLevels() {
		return data.size;
	}

	@Override
	public Map<String, String> getWeaponDataDescriptors(int level) {
		PlaceableWeaponData d = data.get(level);
		Map<String, String> map = new HashMap<String, String>();
		map.put("Type", getClass().getSimpleName());
		map.put("Object", d.getPlacingObject());
		map.put("Ammo Price", d.getAmmoPrice() + "");
		map.put("Max Range", d.getMaxRange() + "");
		map.put("Ammo per clip", d.getMaxAmmo() + "");
		// map.put("Fire rate", (Math.round(100*(60.0)/(d.getPreFireDelay() + d.getPostFireDelay()))/100) + "");
		map.put("Warmup", d.getWarmup() + "s");
		map.put("Reload time", d.getReloadTime() + "");
		return map;
	}

	@Override
	public boolean loadWeapon(JSONObject json) {
		//Call Utility Method
		return WeaponUtils.loadWeapon(json, getClass(), this::loadWeaponData, (nme, desc, i, tg)->{name = nme; description = desc; id = i; tags = tg;});
	}

	protected boolean loadWeaponData(JSONArray arr) {
		data = new Array<PlaceableWeaponData>();

		for (Object obj : arr) {
			JSONObject o = (JSONObject) obj;
			PlaceableWeaponData d = new PlaceableWeaponData(o);
			data.add(d);
		}
		return true;
	}

	@Override
	public List<String> getTags() {
		return tags;
	}

	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public String getStatus(WeaponStack stack) {
		int wTime = stack.getWeaponTime();
		if (wTime == 1 && stack.getPostFire() > 0) {
			return "Reloading";
		} else if (wTime == 2 && stack.getPostFire() > 0) {
			return "Warming Up";
		} else if (wTime == 3 && stack.isPreFiring()) {
			return "Firing";
		} else if (wTime == 4 && stack.getPostFire() > 0) {
			return "Cooling Down";
		}
		return "Ready";
	}
}
