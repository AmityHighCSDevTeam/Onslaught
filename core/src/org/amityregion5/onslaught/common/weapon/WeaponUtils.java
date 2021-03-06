package org.amityregion5.onslaught.common.weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.amityregion5.onslaught.Onslaught;
import org.amityregion5.onslaught.common.func.Consumer3;
import org.amityregion5.onslaught.common.func.Consumer5;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;
import org.amityregion5.onslaught.common.weapon.data.IWeaponDataBase;
import org.amityregion5.onslaught.common.weapon.data.SoundData;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Various utility functions and default methods for weapons
 * 
 * @author sergeys
 *
 */
public class WeaponUtils {
	/**
	 * Purchase ammo for a gun
	 * 
	 * @param player The player
	 * @param stack The WeaponStack
	 * @param maxAmmo The maximum ammo to buy
	 * @param ammoPrice The price per unit of ammo
	 */
	public static void purchaseAmmo(PlayerModel player, WeaponStack stack, int maxAmmo, double ammoPrice) {
		//The price for 1 mag
		double ammoMoney = maxAmmo * ammoPrice;
		//The amount of ammo in a mag
		int amtToBuy = maxAmmo;
		//If player can't afford it
		if (ammoMoney > player.getMoney()) {
			//Figure out the maximum number of ammo that the player can afford
			amtToBuy = (int) (player.getMoney() / ammoMoney);
		}
		//Spend the money
		player.setMoney(player.getMoney() - amtToBuy * ammoPrice);
		//Increase ammo
		stack.setTotalAmmo(stack.getTotalAmmo() + amtToBuy);
	}
	
	/**
	 * Reload a weapon
	 * 
	 * @param stack The WeaponStack
	 * @param game The Game
	 * @param player The Player
	 * @param maxAmmo the maximum ammo in a magazine
	 * @param reloadTime The reload time
	 * @param sounds The list of sounds
	 */
	public static void reload(WeaponStack stack, Game game, PlayerModel player, int maxAmmo, double reloadTime, List<SoundData> sounds) {
		//If ammo is needed
		if (stack.getAmmo() < maxAmmo) {
			//Figure out how much is needed
			int ammoNeeded = maxAmmo - stack.getAmmo();
			//If they dont have enough ammo in storage
			if (ammoNeeded > stack.getTotalAmmo()) {
				//Only take how much ammo they have
				ammoNeeded = stack.getTotalAmmo();
			}
			//If they need ammo
			if (ammoNeeded > 0) {
				//Do reload time
				stack.setPostFire(stack.getPostFire() + reloadTime);
				
				stack.setWeaponTime(1);
				
				//Set the ammo
				stack.setAmmo(stack.getAmmo() + ammoNeeded);
				//Set the total ammo
				stack.setTotalAmmo(stack.getTotalAmmo() - ammoNeeded);

				//Play reload sound
				for (SoundData sound : sounds) {
					if (sound.getTrigger().equals("reload")) {
						game.playSound(sound, player.getEntity().getBody().getWorldCenter());
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param delta the delta time in seconds
	 * @param stack the WeaponStack
	 * @param fireWeaponMethod the fireWeapon method args: fireAtPos, game, firingPlayer, maxFireDegrees, weaponStack
	 */
	public static void tick(float delta, WeaponStack stack, Consumer5<Vector2, Game, PlayerModel, Double, WeaponStack> fireWeaponMethod) {
		//If there is a cooldown
		if (stack.getPostFire() > 0) {
			//Reduce the cooldown
			stack.setPostFire(stack.getPostFire() - delta);
		}
		//If there is warmup
		if (stack.getPreFire() > 0 && stack.isPreFiring()) {
			//Reduce the warmup
			stack.setPreFire(stack.getPreFire() - delta);
			//If ran out of warmup
			if (stack.getPreFire() <= 0) {
				stack.setPreFiring(false);
				//If ammo available
				if (stack.getAmmo() > 0) {
					//Fire
					fireWeaponMethod.run(stack.getWarmupEnd(), stack.getWarmupGame(), stack.getWarmupFiring(), stack.getWarmupMaxFireDegrees(), stack);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param end the point that you are shooting at
	 * @param game the Game object
	 * @param firing the Player that is firing
	 * @param maxFireDegrees the maximum firing degrees
	 * @param stack the WeaponStack
	 * @param isMouseJustDown did the mouse just go down
	 * @param warmupTime the warmup time
	 * @param isAuto is it automatic
	 * @param preFire the prefire delay
	 * @param reload the reload method args: weaponStack, game, firingPlayer
	 * @param fireWeaponMethod the fireWeapon method args: fireAtPos, game, firingPlayer, maxFireDegrees, weaponStack
	 * @return the weapon time value (0 = ignore, 1 = reloading, 2 = warming up, 3 = (pre)firing, 4 = just fired (cooling down)
	 */
	public static void onUse(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack, boolean isMouseJustDown, double warmupTime, boolean isAuto, double preFire, Consumer3<WeaponStack, Game, PlayerModel> reload, Consumer5<Vector2, Game, PlayerModel, Double, WeaponStack> fireWeaponMethod) {
		//If there is no ammo
		if (stack.getAmmo() <= 0) {
			//Reload
			reload.run(stack, game, firing);
			stack.setWeaponTime(1);
			return;
		}
		//If the mouse was just pressed
		if (isMouseJustDown && warmupTime > 0) {
			//Do the warmup
			stack.setPostFire(stack.getPostFire() + warmupTime);
			stack.setWeaponTime(2);
		}
		//If it is either automatic or the mouse was just pressed
		if (isAuto || (!isAuto && isMouseJustDown) && stack.getAmmo() > 0) {
			//Continue firing until the post fire increases above 0
			while (stack.getPostFire() <= 0) {
				//If you have ammo and a pre fire delay
				if (stack.getAmmo() > 0 && preFire > 0 && stack.getPreFire() <= 0) {
					//Do prefire stuffs
					stack.setPreFire(preFire + stack.getPreFire());
					stack.setPreFiring(true);
					stack.setPreFireEnd(end);
					stack.setPreFireGame(game);
					stack.setPreFireFiring(firing);
					stack.setPreFireMaxFireDegrees(maxFireDegrees);
					stack.setWeaponTime(3);
				} else if (stack.getAmmo() > 0 && preFire <= 0) {
					//If you have ammo and no prefire delay
					//Fire
					fireWeaponMethod.run(end, game, firing, maxFireDegrees, stack);
					stack.setWeaponTime(4);
				} else if (!stack.isPreFiring()){
					//If you have no ammo left
					//Reload
					reload.run(stack, game, firing);
					stack.setWeaponTime(1);
					break;
				} else {
					break;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param json the json data
	 * @param clazz the class that we are loading
	 * @param loadWeaponData the loadWeaponData function
	 * @param valReturn a method to recieve loaded values. args: name, desc, id, tags
	 * @return did this suceed
	 */
	public static <T extends IWeaponDataBase> boolean loadWeapon(JsonObject json, Class<?> clazz, Class<T> dataClass, Consumer5<String, String, String, List<String>, List<T>> valReturn) {
		//Load the weapon from JSON
		if ((json.get("className").getAsString()).equals(clazz.getSimpleName())) {
			//Get the name
			String name = Optional.ofNullable(json.get("name").getAsString()).orElse("NAME NOT SET");
			//Get the description
			String description = Optional.ofNullable(json.get("desc").getAsString()).orElse("DESC NOT SET");
			//Get the ID
			String id = Optional.ofNullable(json.get("id").getAsString()).orElse(name);

			//Get the tags
			ArrayList<String> tags = new ArrayList<String>();
			if (json.has("tags")) {
				JsonArray tagArr = json.getAsJsonArray("tags");

				for (JsonElement e : tagArr) {
					tags.add(e.getAsString());
				}
			}

			//Get the weapon array
			JsonArray arr = json.getAsJsonArray("weapon");
			ArrayList<T> weaponData = new ArrayList<T>();

			//If it exists
			if (arr != null) {
				//Load the data
				for (JsonElement e : arr) {
					weaponData.add(Onslaught.instance.gson.fromJson(e, dataClass));
				}
			} else {
				//If doesnt exist log error
				Onslaught.debug(clazz.getSimpleName() + " Loading: Error: Weapon Array does not exist");

				return false;
			}
			
			//Return the values to the class
			valReturn.run(name, description, id, tags, weaponData);

			return true;
		}
		Onslaught.debug(clazz.getSimpleName() + " Loading: Error: Class Name is not " + clazz.getSimpleName());
		return false;
	}
}
