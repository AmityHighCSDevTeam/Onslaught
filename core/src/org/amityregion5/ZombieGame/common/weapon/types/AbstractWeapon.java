package org.amityregion5.ZombieGame.common.weapon.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amityregion5.ZombieGame.ZombieGame;
import org.amityregion5.ZombieGame.common.bullet.BasicBullet;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.data.IWeaponDataBase;
import org.amityregion5.ZombieGame.common.weapon.data.SoundData;
import org.amityregion5.ZombieGame.common.weapon.data.WeaponData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class AbstractWeapon<T extends WeaponData> implements IWeapon {

	// All the variables!
	protected String		name, description, id;
	protected List<String>	tags;
	protected Array<T>		data;

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
		double ammoMoney = data.get(stack.getLevel()).getMaxAmmo() * data.get(stack.getLevel()).getAmmoPrice();
		int amtToBuy = data.get(stack.getLevel()).getMaxAmmo();
		if (ammoMoney > player.getMoney()) {
			amtToBuy = (int) (player.getMoney() / ammoMoney);
		}
		player.setMoney(player.getMoney() - amtToBuy * data.get(stack.getLevel()).getAmmoPrice());
		stack.setTotalAmmo(stack.getTotalAmmo() + amtToBuy);
	}

	@Override
	public void reload(WeaponStack stack, Game game, PlayerModel firing) {
		if (stack.getAmmo() < data.get(stack.getLevel()).getMaxAmmo()) {
			int ammoNeeded = data.get(stack.getLevel()).getMaxAmmo() - stack.getAmmo();
			if (ammoNeeded > stack.getTotalAmmo()) {
				ammoNeeded = stack.getTotalAmmo();
			}
			if (ammoNeeded > 0) {
				stack.setCooldown(stack.getCooldown() + data.get(stack.getLevel()).getReloadTime());
				stack.setAmmo(stack.getAmmo() + ammoNeeded);
				stack.setTotalAmmo(stack.getTotalAmmo() - ammoNeeded);

				for (SoundData sound : data.get(stack.getLevel()).getSounds()) {
					if (sound.getTrigger().equals("reload")) {
						game.playSound(sound, firing.getEntity().getBody().getWorldCenter());
					}
				}
			}
		}
	}

	@Override
	public void tick(float delta, WeaponStack stack) {
		if (stack.getCooldown() > 0) {
			stack.setCooldown(stack.getCooldown() - delta);
		}
		if (stack.getWarmup() > 0 && stack.isWarmingUp()) {
			stack.setWarmup(stack.getWarmup() - delta);
			if (stack.getWarmup() <= 0) {
				stack.setWarmingUp(false);
				fireWeapon(stack.getWarmupEnd(), stack.getWarmupGame(), stack.getWarmupFiring(), stack.getWarmupMaxFireDegrees(), stack);
			}
		}
	}

	@Override
	public void onUse(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack, boolean isMouseJustDown) {
		if (stack.getAmmo() <= 0) {
			reload(stack, game, firing);
			return;
		}
		if (isMouseJustDown) {
			stack.setCooldown(stack.getCooldown() + data.get(stack.getLevel()).getWarmup());
		}
		if (data.get(stack.getLevel()).isAuto() || (!data.get(stack.getLevel()).isAuto() && isMouseJustDown)) {
			while (stack.getCooldown() <= 0) {
				if (stack.getAmmo() > 0 && data.get(stack.getLevel()).getPreFireDelay() > 0 && stack.getWarmup() <= 0) {
					stack.setWarmup(data.get(stack.getLevel()).getPreFireDelay() + stack.getWarmup());
					stack.setWarmingUp(true);
					stack.setWarmupEnd(end);
					stack.setWarmupGame(game);
					stack.setWarmupFiring(firing);
					stack.setWarmupMaxFireDegrees(maxFireDegrees);
				} else if (stack.getAmmo() > 0) {
					fireWeapon(end, game, firing, maxFireDegrees, stack);
				} else {
					reload(stack, game, firing);
					break;
				}
			}
		}
	}

	protected void fireWeapon(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack) {
		stack.setAmmo(stack.getAmmo() - 1);
		double dir = MathHelper.clampAngleAroundCenter(firing.getEntity().getBody().getAngle(),
				MathHelper.getDirBetweenPoints(firing.getEntity().getBody().getPosition(), end), Math.toRadians(maxFireDegrees));

		dir -= Math.toRadians(data.get(stack.getLevel()).getAccuracy() / 2);

		dir += Math.toRadians(game.getRandom().nextDouble() * data.get(stack.getLevel()).getAccuracy());

		dir = MathHelper.fixAngle(dir);

		Vector2 firingPos = firing.getEntity().getBody().getWorldCenter();
		Vector2 firingPosVisual = MathHelper.getEndOfLine(firing.getEntity().getBody().getWorldCenter(), firing.getEntity().getShape().getRadius() - 0.01, dir);

		Vector2 bullVector = VectorFactory.createVector(1000f, (float) dir);

		BasicBullet bull = new BasicBullet(game, firingPosVisual, (float) data.get(stack.getLevel()).getKnockback(),
				(float) data.get(stack.getLevel()).getDamage(), bullVector, firing, data.get(stack.getLevel()).getBulletColor(),
				data.get(stack.getLevel()).getBulletThickness(), 200f);
		bull.setDir((float) dir);

		game.getActiveBullets().add(bull);
		game.getWorld().rayCast(bull, firingPos, bullVector);
		bull.finishRaycast();

		stack.setCooldown(stack.getCooldown() + data.get(stack.getLevel()).getPostFireDelay());

		for (SoundData sound : data.get(stack.getLevel()).getSounds()) {
			if (sound.getTrigger().equals("fire")) {
				game.playSound(sound, firing.getEntity().getBody().getWorldCenter());
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
		WeaponData d = data.get(level);
		Map<String, String> map = new HashMap<String, String>();
		map.put("Type", getClass().getSimpleName());
		map.put("Auto", d.isAuto() + "");
		map.put("Price", d.getPrice() + "");
		map.put("Ammo Price", d.getAmmoPrice() + "");
		map.put("Damage", d.getDamage() + "");
		map.put("Ammo per clip", d.getMaxAmmo() + "");
		map.put("Accuracy", (100 - d.getAccuracy()) + "%");
		map.put("Fire rate", (Math.round(100 * (60.0) / (d.getPreFireDelay() + d.getPostFireDelay())) / 100) + "");
		map.put("Warmup", d.getWarmup() + "s");
		map.put("Reload time", d.getReloadTime() + "");
		map.put("Knockback", d.getKnockback() + "");
		return map;
	}

	@Override
	public boolean loadWeapon(JSONObject json) {
		if (((String) json.get("className")).equals(getClass().getSimpleName())) {
			name = json.containsKey("name") ? (String) json.get("name") : "NAME NOT SET";
			description = json.containsKey("name") ? (String) json.get("desc") : "DESC NOT SET";
			id = json.containsKey("id") ? (String) json.get("id") : name;

			this.tags = new ArrayList<String>();
			if (json.containsKey("tags")) {
				JSONArray tags = (JSONArray) json.get("tags");

				for (Object o : tags) {
					this.tags.add((String) o);
				}
			}

			JSONArray arr = (JSONArray) json.get("weapon");

			if (arr != null) {
				if (!loadWeaponData(arr)) {
					ZombieGame.debug(getClass().getSimpleName() + " Loading: Error: Error loading weapon data");
					return false;
				}
			} else {
				ZombieGame.debug(getClass().getSimpleName() + " Loading: Error: Weapon Array does not exist");

				return false;
			}
			loadWeaponData(arr);

			return true;
		}
		ZombieGame.debug(getClass().getSimpleName() + " Loading: Error: Class Name is not " + getClass().getSimpleName());
		return false;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public List<String> getTags() {
		return tags;
	}

	protected abstract boolean loadWeaponData(JSONArray arr);
}
