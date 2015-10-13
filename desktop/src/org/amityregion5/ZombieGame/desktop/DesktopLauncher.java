package org.amityregion5.ZombieGame.desktop;

import org.amityregion5.ZombieGame.ZombieGame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 1200;
		config.height = 900;
		config.useHDPI = false;
		//config.addIcon("images/iconTestBMP.bmp", FileType.Internal);
		//config.addIcon("images/iconTestBMP1.bmp", FileType.Internal);
		//config.addIcon("images/iconTest.png", FileType.Internal);
		//config.addIcon("images/iconTest3.png", FileType.Internal);
		config.title = "ZombieGame";

		new LwjglApplication(new ZombieGame(false), config);

	}
}
