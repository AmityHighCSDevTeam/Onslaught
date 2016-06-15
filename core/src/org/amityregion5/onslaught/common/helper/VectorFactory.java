package org.amityregion5.onslaught.common.helper;

import com.badlogic.gdx.math.Vector2;

/**
 * A class to help with Vector creation
 * @author sergeys
 *
 */
public class VectorFactory {
	/**
	 * Creates a vector from a magnitude and direction
	 * 
	 * @param mag the magnitude of the vector
	 * @param dir the direction of the vector
	 * @return
	 */
	public static Vector2 createVector(float mag, float dir) {
		//Create a vector that is mag long on the x axis
		Vector2 v = new Vector2(mag, 0);
		//Rotate it by dir radians
		v.rotate((float) Math.toDegrees(dir));
		//Return the vector
		return v;
	}
}
