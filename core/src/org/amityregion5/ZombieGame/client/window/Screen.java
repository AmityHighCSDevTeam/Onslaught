package org.amityregion5.ZombieGame.client.window;

import com.badlogic.gdx.graphics.Camera;

public interface Screen {
	public void drawScreen(float delta, Camera camera);

	public void dispose();
}
