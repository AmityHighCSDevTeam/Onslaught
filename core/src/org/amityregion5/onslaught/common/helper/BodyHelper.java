package org.amityregion5.onslaught.common.helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Methods to help manage bodies
 * @author sergeys
 *
 */
public class BodyHelper {
	/**
	 * Set a body to point at a certain point
	 * @param body the body to rotate
	 * @param coord the coordinate to point at
	 * @param delta the delta time that has occured
	 */
	public static void setPointing(Body body, Vector2 coord, float delta) {
		//Call main method
		setPointing(body, coord, delta, 1);
	}

	/**
	 * Set a body to point at a certain point
	 * @param body the body to rotate
	 * @param coord the coordinate to point at
	 * @param delta the delta time that has occured
	 * @param timeFactor a factor determining turn acceleration. Smaller = Faster
	 */
	public static void setPointing(Body body, Vector2 coord, float delta, double timeFactor) {
		//Calculate the next angle the body will be at
		double nextAngle = body.getAngle() + body.getAngularVelocity() / (1.0 / delta) * timeFactor;
		//The total rotation that still has to occur
		double totalRotation = MathHelper.getDirBetweenPoints(body.getWorldCenter(), coord) - nextAngle;
		
		//Fix the angle
		while (totalRotation < Math.toRadians(-180)) {
			totalRotation += Math.toRadians(360);
		}
		//Fix the angle
		while (totalRotation > Math.toRadians(180)) {
			totalRotation -= Math.toRadians(360);
		}
		
		//Get the desired angular velocity  that we have
		double desiredAngularVelocity = totalRotation * (1.0 / delta);
		//Calculate the impulse that needs to be applied
		double impulse = body.getInertia() * desiredAngularVelocity / timeFactor;// disregard time factor
		//Apply the implulse
		body.applyAngularImpulse((float) impulse, true);
	}
}
