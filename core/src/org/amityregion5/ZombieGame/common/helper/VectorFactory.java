package org.amityregion5.ZombieGame.common.helper;

import com.badlogic.gdx.math.Vector2;

public class VectorFactory {
	public static Vector2 createVector(float speed, float angle) {
		Vector2 v = new Vector2(1, 0).scl(speed);
		v.rotate((float) Math.toDegrees(angle));
		return v;
	}
}
