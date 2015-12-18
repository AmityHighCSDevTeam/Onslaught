package org.amityregion5.ZombieGame.common;

import com.badlogic.gdx.graphics.Color;

/**
 * A file of constants
 * 
 * Many things should be in here that aren't
 * 
 * @author sergeys
 *
 */
public class Constants {

	//The time step for the game (updates per second)
	public static final float	TIME_STEP			= 1f / 60f;
	//Velocity iterations per update
	public static final int		VELOCITY_ITERATIONS	= 6;
	//Position iterations per update
	public static final int		POSITION_ITERATIONS	= 2;

	//Actual White. THis has been phased out
	public static final Color TRUE_WHITE = new Color(1, 1, 1, 1);
}
