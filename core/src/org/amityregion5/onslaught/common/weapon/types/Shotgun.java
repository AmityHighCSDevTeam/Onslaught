package org.amityregion5.onslaught.common.weapon.types;

import java.util.Map;

import org.amityregion5.onslaught.common.bullet.BasicBullet;
import org.amityregion5.onslaught.common.game.Game;
import org.amityregion5.onslaught.common.game.model.entity.PlayerModel;
import org.amityregion5.onslaught.common.helper.MathHelper;
import org.amityregion5.onslaught.common.helper.VectorFactory;
import org.amityregion5.onslaught.common.weapon.WeaponStack;
import org.amityregion5.onslaught.common.weapon.data.ShotgunWeaponData;
import org.amityregion5.onslaught.common.weapon.data.SoundData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A class for a shotgun type weapon
 * 
 * @author sergeys
 */
public class Shotgun extends AbstractWeapon<ShotgunWeaponData> {

	@Override
	protected void fireWeapon(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack) {
		//Decrease the ammo
		stack.setAmmo(stack.getAmmo() - 1);
		//Get the main firing direction
		double dir = MathHelper.clampAngleAroundCenter(firing.getEntity().getBody().getAngle(),
				MathHelper.getDirBetweenPoints(firing.getEntity().getBody().getPosition(), end), Math.toRadians(maxFireDegrees));

		//Loop through for each shot that it shoots
		for (int i = 0; i < data.get(stack.getLevel()).getShots(); i++) {
			//Get the index of the side of the center that it should be on
			double dirDel = i - data.get(stack.getLevel()).getShots() / 2;
			//Convert it to degrees
			dirDel *= data.get(stack.getLevel()).getSpread();

			//Add the directions together and convert above to radians
			double newDir = dir + Math.toRadians(dirDel);

			//Add accuracy to the shot
			newDir -= Math.toRadians(data.get(stack.getLevel()).getAccuracy() / 2);
			newDir += Math.toRadians(game.getRandom().nextDouble() * data.get(stack.getLevel()).getAccuracy());

			//Fix the direction
			newDir = MathHelper.fixAngle(newDir);

			//Starting position of bullet
			Vector2 firingPos = firing.getEntity().getBody().getWorldCenter();
			//Visual starting position
			Vector2 firingPosVisual = MathHelper.getEndOfLine(firing.getEntity().getBody().getWorldCenter(), firing.getEntity().getShape().getRadius() - 0.01,
					dir);

			//The bullet's raycast vector
			Vector2 bullVector = VectorFactory.createVector(1000f, (float) newDir);

			//Create the bullet
			BasicBullet bull = new BasicBullet(game, firingPosVisual, (float) data.get(stack.getLevel()).getKnockback(),
					(float) data.get(stack.getLevel()).getDamage(), bullVector, firing, data.get(stack.getLevel()).getBulletColor(),
					data.get(stack.getLevel()).getBulletThickness(), 200f);
			//Set its direction
			bull.setDir((float) newDir);

			game.runAfterNextTick(()->{
				game.getActiveBullets().add(bull);
				game.getWorld().rayCast(bull, firingPos, bullVector);
				bull.finishRaycast();
			});
		}

		//Increase cooldown
		stack.setPostFire(stack.getPostFire() + data.get(stack.getLevel()).getPostFireDelay());

		//Play all fire sounds
		for (SoundData sound : data.get(stack.getLevel()).getSounds()) {
			if (sound.getTrigger().equals("fire")) {
				game.playSound(sound, firing.getEntity().getBody().getWorldCenter());
			}
		}
	}

	@Override
	protected boolean loadWeaponData(JSONArray arr) {
		//Load the gun's data as ShotgunWeaponData
		data = new Array<ShotgunWeaponData>();

		for (Object obj : arr) {
			JSONObject o = (JSONObject) obj;
			ShotgunWeaponData d = new ShotgunWeaponData(o);
			data.add(d);
		}
		return true;
	}

	@Override
	public Map<String, String> getWeaponDataDescriptors(int level) {
		//Add bullets and bullet spread to the descriptors
		Map<String, String> map = super.getWeaponDataDescriptors(level);

		map.put("Bullets", data.get(level).getShots() + "");
		map.put("Bullet Spread", data.get(level).getSpread() + "");

		return map;
	}
}
