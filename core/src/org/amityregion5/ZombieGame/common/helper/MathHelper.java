package org.amityregion5.ZombieGame.common.helper;

import com.badlogic.gdx.math.Vector2;

/**
 * A class with mathmatical helper functions
 * 
 * @author sergeys
 *
 */
public class MathHelper {
	//The radian value of 180 degrees (also known as PI)
	public static final double rad180 = Math.toRadians(180);

	/**
	 * Get the angle between two points
	 * 
	 * @param start the start point
	 * @param end the end point
	 * @return the angle in radians
	 */
	public static double getDirBetweenPoints(Vector2 start, Vector2 end) {
		//If both points are null return 0
		if (start == null || end == null) { return 0; }
		//Else calculate the angle
		return getDirBetweenPoints(start.x, start.y, end.x, end.y);
	}

	/**
	 * Get the angle between 2 points
	 * 
	 * @param x1 x value of start point
	 * @param y1 y value of start point
	 * @param x2 x value of end point
	 * @param y2 y value of end point
	 * @return the angle in radians
	 */
	public static double getDirBetweenPoints(double x1, double y1, double x2, double y2) {
		//atan2(∆y, ∆x)
		return Math.atan2(y2 - y1, x2 - x1);
	}

	/**
	 * Get a vector representing the point at the end of the line specified by the start point, length, and direction
	 * @param start the start point
	 * @param len the length of the line
	 * @param dir the direction of the line
	 * @return the end point
	 */
	public static Vector2 getEndOfLine(Vector2 start, double len, double dir) {
		//Calculate end point
		return new Vector2((float) (start.x + len * Math.cos(dir)), (float) (start.y + len * Math.sin(dir)));
	}

	/**
	 * Clamp a value between two other values
	 * 
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param val the value to clamp
	 * @return the value after it has been clamped
	 */
	public static double clamp(double min, double max, double val) {
		if (val < min) { return min; }
		if (val > max) { return max; }
		return val;
	}

	/**
	 * Fixes an angle back to [-π,π]
	 * @param rad the angle in radians
	 * @return the fixed angle in radians
	 */
	public static double fixAngle(double rad) {
		//Rotate it back if it is past -180
		while (rad < Math.toRadians(-180)) {
			rad += Math.toRadians(360);
		}
		//Rotate it back if it is past 180
		while (rad > Math.toRadians(180)) {
			rad -= Math.toRadians(360);
		}
		//Return the value
		return rad;
	}

	/**
	 * Clamp and angle around a center angle
	 * @param center the center of the angle
	 * @param angle the angle to clamp
	 * @param clampRadians the maximum angle on either side of center
	 * @return the clamped angle
	 */
	public static double clampAngleAroundCenter(double center, double angle, double clampRadians) {
		center = MathHelper.fixAngle(center);

		boolean flipFinalResult = false;

		if (Math.abs(center - angle) > Math.toRadians(180)) {
			flipFinalResult = true;
			if (center > angle) {
				center -= rad180;
				angle += rad180;
			} else {
				center += rad180;
				angle -= rad180;
			}
		}

		double diff = angle - center;

		diff = MathHelper.clamp(-clampRadians, clampRadians, diff);

		center = MathHelper.fixAngle(center + diff);

		if (flipFinalResult) {
			center -= rad180;
		}

		return center;
	}
}
