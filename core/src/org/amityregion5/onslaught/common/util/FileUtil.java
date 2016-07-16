package org.amityregion5.onslaught.common.util;

import org.amityregion5.onslaught.Onslaught;

import com.badlogic.gdx.files.FileHandle;

public class FileUtil {
	public static FileHandle getHandle(String fileDir) {
		return Onslaught.instance.gameData.child(fileDir);
	}
}
