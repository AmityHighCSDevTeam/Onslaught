package org.amityregion5.ZombieGame.desktop;

import org.amityregion5.ZombieGame.ZombieGame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 1200;
		config.height = 900;

		new LwjglApplication(new ZombieGame(false), config);
	}
}
