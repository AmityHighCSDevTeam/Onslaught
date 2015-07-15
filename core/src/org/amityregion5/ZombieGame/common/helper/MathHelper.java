package org.amityregion5.ZombieGame.common.helper;

import com.badlogic.gdx.math.Vector2;

public class MathHelper {
	public static final double	rad180	= Math.toRadians(180);

	public static double getDirBetweenPoints(Vector2 start, Vector2 end) {
		if (start == null || end == null) {
			return 0;
		}
		return getDirBetweenPoints(start.x, start.y, end.x, end.y);
	}

	public static double getDirBetweenPoints(double x1, double y1, double x2,
			double y2) {
		return Math.atan2(y2 - y1, x2 - x1);
	}

	public static Vector2 getEndOfLine(Vector2 start, double len, double dir) {
		return new Vector2((float) (start.x + len * Math.cos(dir)),
				(float) (start.y + len * Math.sin(dir)));
	}

	public static double clamp(double min, double max, double val) {
		if (val < min) { return min; }
		if (val > max) { return max; }
		return val;
	}

	public static double fixAngle(double rad) {
		while (rad < Math.toRadians(-180)) {
			rad += Math.toRadians(360);
		}
		while (rad > Math.toRadians(180)) {
			rad -= Math.toRadians(360);
		}
		return rad;
	}

	public static double clampAngleAroundCenter(double center, double angle,
			double clampRadians) {
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
