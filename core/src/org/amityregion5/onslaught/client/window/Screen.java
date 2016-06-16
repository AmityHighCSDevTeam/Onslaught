package org.amityregion5.onslaught.client.window;

import com.badlogic.gdx.graphics.Camera;

/**
 * An interface for a simple overlay screen for the game
 * @author sergeys
 *
 */
public interface Screen {
	/**
	 * Called to draw graphics to the screen
	 * 
	 * @param delta the time since the last frame
	 * @param camera the camera used by the game
	 */
	public void drawScreen(float delta, Camera camera);

	/**
	 * Called to clean up data when this object is no longer needed
	 */
	public void dispose();
	
	/** 
	 * Should the game pause if this screen is open as a window
	 * 
	 * @return a boolean value representing the above
	 */
	public boolean pauseIfOpenAsWindow();
}
