package org.amityregion5.ZombieGame.common.util;

import org.amityregion5.ZombieGame.ZombieGame;

import com.badlogic.gdx.files.FileHandle;

public class FileUtil {
	public static FileHandle getHandle(String fileDir) {
		return ZombieGame.instance.gameData.child(fileDir);
	}
}
