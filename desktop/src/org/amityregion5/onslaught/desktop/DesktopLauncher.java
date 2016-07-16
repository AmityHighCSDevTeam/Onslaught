package org.amityregion5.onslaught.desktop;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.amityregion5.onslaught.Onslaught;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) throws FileNotFoundException {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 1600;
		config.height = 900;
		config.useHDPI = true;
		config.vSyncEnabled = true;
		
		config.addIcon("icons/Size16.png", FileType.Internal);
		config.addIcon("icons/Size32.png", FileType.Internal);
		config.addIcon("icons/Size128.png", FileType.Internal);
		
		config.title = "Onslaught";
		
		List<String> args = Arrays.asList(arg);

		new LwjglApplication(new Onslaught(false, args.contains("--AllowCheatMode")), config);

	}
}
