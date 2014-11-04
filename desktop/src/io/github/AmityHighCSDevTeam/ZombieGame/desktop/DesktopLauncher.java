package io.github.AmityHighCSDevTeam.ZombieGame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.AmityHighCSDevTeam.ZombieGame.ZombieGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 1200;
		config.height = 900;

		new LwjglApplication(new ZombieGame(false), config);
	}
}
