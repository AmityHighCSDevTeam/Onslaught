package org.amityregion5.onslaught.common.weapon.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amityregion5.onslaught.common.entity.EntityRocket;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;
import org.amityregion5.onslaught.common.game.model.entity.RocketModel;
import org.amityregion5.onslaught.common.helper.MathHelper;
import org.amityregion5.onslaught.common.helper.VectorFactory;
import org.amityregion5.onslaught.common.weapon.WeaponStack;
import org.amityregion5.onslaught.common.weapon.WeaponStatus;
import org.amityregion5.onslaught.common.weapon.WeaponUtils;
import org.amityregion5.onslaught.common.weapon.data.IWeaponDataBase;
import org.amityregion5.onslaught.common.weapon.data.RocketData;
import org.amityregion5.onslaught.common.weapon.data.SoundData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Rocket implements IWeapon {

	protected String name; //The name of the rocket
	protected String description; //The description
	protected String id; //The unique ID
	protected String pathName;
	protected List<String>		tags; //The tags owned by this rocket
	protected Array<RocketData>	data; //The rocket's data

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
		WeaponUtils.tick(delta, stack, this::fireWeapon);
	}

	@Override
	public void onUse(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack, boolean isMouseJustDown) {
		//Call utility function
		WeaponUtils.onUse(end, game, firing, maxFireDegrees, stack, isMouseJustDown, data.get(stack.getLevel()).getWarmup(),
				data.get(stack.getLevel()).isAuto(), data.get(stack.getLevel()).getPreFireDelay(),
				this::reload, this::fireWeapon);
	}

	protected void fireWeapon(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack) {

		//Get the rocket data
		RocketData gData = data.get(stack.getLevel());

		//Decrease ammo
		stack.setAmmo(stack.getAmmo() - 1);

		//Get firing direction
		double dir = MathHelper.clampAngleAroundCenter(firing.getEntity().getBody().getAngle(),
				MathHelper.getDirBetweenPoints(firing.getEntity().getBody().getPosition(), end), Math.toRadians(maxFireDegrees));

		//Calculate new direction based on accuracy
		dir -= Math.toRadians(data.get(stack.getLevel()).getAccuracy() / 2);
		dir += Math.toRadians(game.getRandom().nextDouble() * data.get(stack.getLevel()).getAccuracy());

		//Fix the direction
		dir = MathHelper.fixAngle(dir);

		//The fly sound
		SoundData flySound = null;

		//Get the first fly sound
		for (SoundData sound : gData.getSounds()) {
			if (sound.getTrigger().equals("fly")) {
				flySound = sound;
				break;
			}
		}

		//Create a rocket model
		RocketModel rocketModel = new RocketModel(new EntityRocket((float) gData.getSize()), game, firing, gData.getFieldTextureString(),
				(float) gData.getSize(), flySound);

		//Set strength
		rocketModel.setStrength(gData.getStrength());
		//Set acceleration
		rocketModel.setAcceleration((float) gData.getAcceleration());
		//Set time until explosion
		rocketModel.setTimeUntilExplosion((float) gData.getFuseTime());

		//Get player's position
		Vector2 playerPos = firing.getEntity().getBody().getWorldCenter();

		//Get the new position taking into account player size and rocket size
		Vector2 pos = VectorFactory.createVector(0.18f + (float) gData.getSize() * 2, (float) dir);

		//Add the rocket to the world
		game.addEntityToWorld(rocketModel, pos.x + playerPos.x, pos.y + playerPos.y);

		//Set its speed
		rocketModel.getEntity().getBody().applyForceToCenter(VectorFactory.createVector((float) gData.getThrowSpeed(), (float) dir), true);
		//Set its facing direction
		rocketModel.getEntity().getBody().setTransform(rocketModel.getEntity().getBody().getWorldCenter(), (float) dir);
		//Set post fire delay
		stack.setPostFire(stack.getPostFire() + gData.getPostFireDelay());

		//Play all fire sounds
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
		RocketData d = data.get(level);

		Map<String, String> map = new HashMap<String, String>();
		map.put("Type", getClass().getSimpleName());
		map.put("Auto", d.isAuto() + "");
		map.put("Ammo Price", d.getAmmoPrice() + "");
		map.put("Size", d.getSize() + "");
		map.put("Strength", d.getStrength() + "");
		map.put("Max Fuse Time", d.getFuseTime() + "");
		map.put("Acceleration", d.getAcceleration() + "");
		map.put("Ammo per clip", d.getMaxAmmo() + "");
		map.put("Accuracy", (100 - d.getAccuracy()) + "%");
		map.put("Fire rate", (Math.round(100 * (60.0) / (d.getPreFireDelay() + d.getPostFireDelay())) / 100) + "");
		map.put("Warmup", d.getWarmup() + "s");
		map.put("Reload time", d.getReloadTime() + "");
		return map;
	}

	@Override
	public boolean loadWeapon(JSONObject json, String pathName) {
		//Call Utility Method
		this.pathName = pathName;
		return WeaponUtils.loadWeapon(json, getClass(), this::loadWeaponData, (nme, desc, i, tg)->{name = nme; description = desc; id = i; tags = tg;});

	}
	
	@Override
	public String getPathName() {
		return pathName;
	}

	protected boolean loadWeaponData(JSONArray arr) {
		//Load weapon data
		data = new Array<RocketData>();

		for (Object obj : arr) {
			JSONObject o = (JSONObject) obj;
			RocketData d = new RocketData(o);
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
	public WeaponStatus getStatus(WeaponStack stack) {
		int wTime = stack.getWeaponTime();
		if (wTime == 1 && stack.getPostFire() > 0) {
			return WeaponStatus.RELOAD;
		} else if (wTime == 2 && stack.getPostFire() > 0) {
			return WeaponStatus.WARMUP;
		} else if (wTime == 3 && stack.isPreFiring()) {
			return WeaponStatus.FIRE;
		} else if (wTime == 4 && stack.getPostFire() > 0) {
			return WeaponStatus.COOLDOWN;
		}
		return WeaponStatus.READY;
	}
}
