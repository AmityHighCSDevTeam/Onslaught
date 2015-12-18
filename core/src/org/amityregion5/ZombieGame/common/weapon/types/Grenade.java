package org.amityregion5.ZombieGame.common.weapon.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amityregion5.ZombieGame.common.entity.EntityGrenade;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.entity.GrenadeModel;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.WeaponUtils;
import org.amityregion5.ZombieGame.common.weapon.data.GrenadeData;
import org.amityregion5.ZombieGame.common.weapon.data.IWeaponDataBase;
import org.amityregion5.ZombieGame.common.weapon.data.SoundData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Grenade implements IWeapon {

	// All the variables!
	protected String				name, description, id;
	protected List<String>			tags;
	protected Array<GrenadeData>	data;

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
		//Get grenade data
		GrenadeData gData = data.get(stack.getLevel());

		//Decrement ammo
		stack.setAmmo(stack.getAmmo() - 1);

		//Calculate direction
		double dir = MathHelper.clampAngleAroundCenter(firing.getEntity().getBody().getAngle(),
				MathHelper.getDirBetweenPoints(firing.getEntity().getBody().getPosition(), end), Math.toRadians(maxFireDegrees));

		//Take accuracy into account
		dir -= Math.toRadians(data.get(stack.getLevel()).getAccuracy() / 2);
		dir += Math.toRadians(game.getRandom().nextDouble() * data.get(stack.getLevel()).getAccuracy());

		//Fix angel
		dir = MathHelper.fixAngle(dir);

		//Create the grenade model
		GrenadeModel grenadeModel = new GrenadeModel(new EntityGrenade((float) gData.getSize()), game, firing, gData.getFieldTextureString());

		//Set strength
		grenadeModel.setStrength(gData.getStrength());
		//Set fuse time
		grenadeModel.setTimeUntilExplosion((float) gData.getFuseTime());

		//Player position
		Vector2 playerPos = firing.getEntity().getBody().getWorldCenter();

		//Difference between player position and new position
		Vector2 pos = VectorFactory.createVector(0.16f + (float) gData.getSize() * 2, (float) dir);

		//Place the grenade into the world
		game.addEntityToWorld(grenadeModel, pos.x + playerPos.x, pos.y + playerPos.y);

		//Apply the throw speed
		grenadeModel.getEntity().getBody().applyForceToCenter(VectorFactory.createVector((float) gData.getThrowSpeed(), (float) dir), true);
		//Set fire delay
		stack.setPostFire(stack.getPostFire() + gData.getPostFireDelay());

		//Play fire sounds
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
		GrenadeData d = data.get(level);

		Map<String, String> map = new HashMap<String, String>();
		map.put("Type", getClass().getSimpleName());
		map.put("Auto", d.isAuto() + "");
		map.put("Ammo Price", d.getAmmoPrice() + "");
		map.put("Size", d.getSize() + "");
		map.put("Strength", d.getStrength() + "");
		map.put("Fuse Time", d.getFuseTime() + "");
		map.put("Ammo per clip", d.getMaxAmmo() + "");
		map.put("Accuracy", (100 - d.getAccuracy()) + "%");
		map.put("Fire rate", (Math.round(100 * (60.0) / (d.getPreFireDelay() + d.getPostFireDelay())) / 100) + "");
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
		data = new Array<GrenadeData>();

		for (Object obj : arr) {
			JSONObject o = (JSONObject) obj;
			GrenadeData d = new GrenadeData(o);
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
}
