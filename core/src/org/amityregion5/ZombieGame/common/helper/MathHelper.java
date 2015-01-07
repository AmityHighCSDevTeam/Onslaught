package org.amityregion5.ZombieGame.common.helper;

import com.badlogic.gdx.math.Vector2;

public class MathHelper {
	
	public static double getDirBetweenPoints(Vector2 start, Vector2 end) {
		return getDirBetweenPoints(start.x, start.y, end.x, end.y);
	}
	public static double getDirBetweenPoints(double x1, double y1, double x2, double y2) {
		return Math.atan2(y2 - y1, x2 - x1);
	}
	public static Vector2 getEndOfLine(Vector2 start, double len, double dir) {
		return new Vector2((float)(start.x + len * Math.cos(dir)), (float)(start.y + len * Math.sin(dir)));
	}
	
	
}
