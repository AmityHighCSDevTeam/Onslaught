package org.amityregion5.ZombieGame.common.weapon.types;

import java.util.Map;

import org.amityregion5.ZombieGame.common.bullet.BasicBullet;
import org.amityregion5.ZombieGame.common.game.Game;
import org.amityregion5.ZombieGame.common.game.model.entity.PlayerModel;
import org.amityregion5.ZombieGame.common.helper.MathHelper;
import org.amityregion5.ZombieGame.common.helper.VectorFactory;
import org.amityregion5.ZombieGame.common.weapon.WeaponStack;
import org.amityregion5.ZombieGame.common.weapon.data.ShotgunWeaponData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 *
 * @author sergeys
 *
 */
public class Shotgun extends AbstractWeapon<ShotgunWeaponData> {

	@Override
	protected void fireWeapon(Vector2 end, Game game, PlayerModel firing, double maxFireDegrees, WeaponStack stack) {
		stack.setAmmo(stack.getAmmo() - 1);
		double dir = MathHelper.clampAngleAroundCenter(firing
				.getEntity().getBody().getAngle(), MathHelper
				.getDirBetweenPoints(
						firing.getEntity().getBody().getPosition(), end), Math
						.toRadians(maxFireDegrees));

		
		for (int i = 0; i<data.get(stack.getLevel()).getShots(); i++) {
			double dirDel = i-data.get(stack.getLevel()).getShots()/2;
			dirDel *= data.get(stack.getLevel()).getSpread();
			
			double newDir = dir + Math.toRadians(dirDel);
			
			newDir -= Math.toRadians(data.get(stack.getLevel())
					.getAccuracy() / 2);

			newDir += Math.toRadians(game.getRandom().nextDouble()
					* data.get(stack.getLevel()).getAccuracy());

			newDir = MathHelper.fixAngle(newDir);
			
			Vector2 v = MathHelper.getEndOfLine(firing.getEntity().getBody()
					.getPosition(),
					firing.getEntity().getShape().getRadius() - 0.01, newDir);

			Vector2 bullVector = VectorFactory.createVector(200f,
					(float) newDir);

			BasicBullet bull = new BasicBullet(game, v, (float) data
					.get(stack.getLevel()).getKnockback(), (float) data
					.get(stack.getLevel()).getDamage(), bullVector, firing, 
					data.get(stack.getLevel()).getBulletColor(),
					data.get(stack.getLevel()).getBulletThickness());
			bull.setDir((float) newDir);

			game.getActiveBullets().add(bull);
			game.getWorld().rayCast(bull, v, bullVector);
			bull.finishRaycast();
		}

		stack.setCooldown(stack.getCooldown()
				+ data.get(stack.getLevel()).getPostFireDelay());	}
	
	@Override
	protected boolean loadWeaponData(JSONArray arr) {
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
		Map<String, String> map = super.getWeaponDataDescriptors(level);
		
		map.put("Bullets", data.get(level).getShots() + "");
		map.put("Bullet Spread", data.get(level).getSpread() + "");

		
		return map;
	}
}
