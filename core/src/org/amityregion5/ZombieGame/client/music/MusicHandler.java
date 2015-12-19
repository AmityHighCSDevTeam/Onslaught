package org.amityregion5.ZombieGame.client.music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.amityregion5.ZombieGame.ZombieGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicHandler {

	private static String currentMusicPlaying = "";
	private static Music currentMusic;

	public static final String menuMusic = "*/Music/Menu/*";
	public static final String gameMusic = "*/Music/Game/*";
	public static final String noMusic = "";

	public static void setMusicPlaying(final String music) {
		Thread startMusicThread = new Thread(()-> {
			if (!currentMusicPlaying.matches(regexify(music)) && currentMusic != null) {
				currentMusic.stop();
				currentMusic.dispose();
				currentMusic = null;
			}
			if (currentMusicPlaying.matches(regexify(music))) {
				return;
			}

			List<String> files = getAllMatchedFiles(regexify(music));

			String file = files.get(new Random().nextInt(files.size()));

			currentMusicPlaying = file;
			
			String extensionLess = ZombieGame.instance.gameData.child(file).pathWithoutExtension();
			
			currentMusic = Gdx.audio.newMusic(ZombieGame.instance.gameData.child(file));
			
			if (Gdx.files.absolute(extensionLess + ".volume").exists()) {
				float volLevel = Float.parseFloat(Gdx.files.absolute(extensionLess + ".volume").readString());
				currentMusic.setVolume(volLevel);
			}
			
			currentMusic.play();
			ZombieGame.debug("Playing Music: " + file + " at volume " + currentMusic.getVolume());

			currentMusic.setOnCompletionListener((m)->{
				m.stop();
				m.dispose();
				currentMusic = null;
				setMusicPlaying(music);
			});
		}, "Start Music Thread");

		startMusicThread.setDaemon(true);
		startMusicThread.start();
	}

	public static Music getCurrentMusic() {
		return currentMusic;
	}

	private static List<String> getAllMatchedFiles(String str) {
		List<String> allFiles = getLeafFiles(ZombieGame.instance.gameData.file(), "");

		allFiles = allFiles.parallelStream().filter((s)->s.matches(str)).collect(Collectors.toList());
		allFiles = allFiles.parallelStream().filter((s)->s.endsWith(".mp3") || s.endsWith(".wav")).collect(Collectors.toList());

		return allFiles;
	}

	private static List<String> getLeafFiles(File dir, String prev) {
		File[] subFiles = dir.listFiles();
		List<String> files = new ArrayList<String>();

		for (File f : subFiles) {
			if (!f.isHidden() && f.exists()) {
				if (f.isDirectory()) {
					files.addAll(getLeafFiles(f, prev + f.getName() + "/"));
				} else if (f.isFile()) {
					files.add(prev + f.getName());
				}
			}
		}

		return files;
	}

	private static String regexify(String str) {
		if (str == null) { return ""; }
		String finalString = "";

		String[] split = str.split(Pattern.quote("**"));
		for (int i = 0; i < split.length; i++) {
			String[] split2 = split[i].split(Pattern.quote("*"));
			for (int i2 = 0; i2 < split2.length; i2++) {
				String[] split3 = split2[i2].split(Pattern.quote("?"));
				for (int i3 = 0; i3 < split3.length; i3++) {
					finalString += split3[i3] + (i3 == split3.length - 1 ? "" : "[^/]?");
				}
				finalString += (split2[i2].endsWith("?") ? "[^/]?" : "");
				finalString += (i2 == split2.length - 1 ? "" : "[^/]*");
			}
			finalString += (split[i].endsWith("*") ? "[^/]*" : "");
			finalString += (i == split.length - 1 ? "" : ".*");
		}
		finalString += (str.endsWith("**") ? ".*" : "");

		return finalString;
	}
	
	public static void dispose() {
		currentMusic.stop();
		currentMusic.dispose();
	}
}
