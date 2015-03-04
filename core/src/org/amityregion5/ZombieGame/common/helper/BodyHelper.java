package org.amityregion5.ZombieGame.common.helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class BodyHelper {
	public static void setPointing(Body body, Vector2 coord, float delta) {
		setPointing(body, coord, delta, 1);
	}
	public static void setPointing(Body body, Vector2 coord, float delta, double timeFactor) {
		double nextAngle = body.getAngle() + body.getAngularVelocity() / (1.0 / delta) * timeFactor;
		double totalRotation = MathHelper.getDirBetweenPoints(body.getWorldCenter(), coord) - nextAngle;
		while ( totalRotation < Math.toRadians(-180)) totalRotation += Math.toRadians(360);
		while ( totalRotation > Math.toRadians(180)) totalRotation -= Math.toRadians(360);
		double desiredAngularVelocity = totalRotation * (1.0 / delta);
		double impulse = body.getInertia() * desiredAngularVelocity / timeFactor;// disregard time factor
		body.applyAngularImpulse((float) impulse, true);
	}
}
